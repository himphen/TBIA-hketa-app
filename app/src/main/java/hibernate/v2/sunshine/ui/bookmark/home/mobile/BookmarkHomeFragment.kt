package hibernate.v2.sunshine.ui.bookmark.home.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.himphen.logger.Logger
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentBookmarkHomeBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.bookmark.EtaCardViewType
import hibernate.v2.sunshine.ui.bookmark.edit.mobile.BookmarkEditActivity
import hibernate.v2.sunshine.ui.bookmark.home.BookmarkHomeViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.route.list.mobile.RouteListActivity
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.GeneralUtils.REFRESH_TIME
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.tickerFlow
import hibernate.v2.sunshine.util.toggleSlideUp
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class BookmarkHomeFragment : BaseFragment<FragmentBookmarkHomeBinding>() {

    companion object {
        fun getInstance() = BookmarkHomeFragment()
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val viewModel by inject<BookmarkHomeViewModel>()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private val adapter = BookmarkHomeEtaCardAdapter(
        sharedPreferencesManager.etaCardType,
        sharedPreferencesManager.hideAdBanner > System.currentTimeMillis(),
        onAddButtonClick = {
            (activity as? MainActivity)?.etaUpdatedLauncher?.launch(
                Intent(
                    context,
                    RouteListActivity::class.java
                )
            )
        },
        onEditButtonClick = {
            (activity as? MainActivity)?.etaUpdatedLauncher?.launch(
                Intent(
                    context,
                    BookmarkEditActivity::class.java
                )
            )
        }
    )

    private var etaCardList: MutableList<Card.EtaCard>? = null

    private var etaRequestJob: Job? = null

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            hideUpdateEtaFailedText()

            etaCardList?.let { etaCardList ->
                etaCardList.clear()
                etaCardList.addAll(it)
                processEtaList()
            } ?: run {
                etaCardList = it.toMutableList()
                adapter.setData(etaCardList)
                processEtaList()

                lifecycleScope.launch {
                    (activity as? MainActivity)?.isResetLoadingBookmarkList = false
                    viewModel.etaRequested.emit(true)
                }
            }
        }

        mainViewModel.onUpdatedEtaList.onEach {
            hideUpdateEtaFailedText()
            updateAdapterData()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        mainViewModel.onUpdatedEtaLayout.onEach {
            hideUpdateEtaFailedText()
            initAdapter()
            updateAdapterData()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.etaUpdateError.onEach {
            viewBinding?.updateEtaFailedTv?.apply {
                toggleSlideUp(true)
                text = getString(R.string.text_eta_loading_failed, 400)
                Logger.e(it, "Error")
            }

//            Firebase.crashlytics.recordException(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.etaRequested.onEach {
            if (it && (activity as? MainActivity)?.isResetLoadingBookmarkList == false) {
                etaRequestJob = viewModel.updateEtaList()
            } else {
                etaRequestJob?.cancel()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Logger.t("lifecycle").d("repeatOnLifecycle")
                tickerFlow(REFRESH_TIME.milliseconds).collect {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initUi()
        initData()
    }

    private fun initData() {
        viewModel.getEtaListFromDb()
    }

    private fun initUi() {
        viewBinding?.apply {
            emptyViewCl.emptyDescTv.text = getString(R.string.empty_eta_list)
            emptyViewCl.addStopButton.visible()
            emptyViewCl.addStopButton.setOnClickListener {
                (activity as? MainActivity)?.etaUpdatedLauncher?.launch(
                    Intent(
                        context,
                        RouteListActivity::class.java
                    )
                )
            }
            (recyclerView.itemAnimator as? SimpleItemAnimator)
                ?.supportsChangeAnimations = false
        }
        initAdapter()
    }

    private fun processEtaList() {
        val etaCardList = etaCardList
        if (etaCardList.isNullOrEmpty()) {
            viewBinding?.apply {
                emptyViewCl.root.visible()
                recyclerView.gone()
            }
        } else {
            viewBinding?.apply {
                emptyViewCl.root.gone()
                recyclerView.visible()
            }
            etaCardList.forEachIndexed { index, etaCard ->
                val temp = etaCard.etaList.filter { eta: TransportEta ->
                    eta.eta?.let { etaDate ->
                        val currentDate = Date()
                        DateUtil.getTimeDiffInMin(
                            etaDate,
                            currentDate
                        ) > 0
                    } ?: run {
                        false
                    }
                }

                etaCard.etaList.clear()
                etaCard.etaList.addAll(temp)

                adapter.replace(index, etaCard)
            }
        }
    }

    private fun initAdapter() {
        Logger.d("updateAdapterViewType")
        val viewBinding = viewBinding ?: return
        val context = context ?: return
        viewBinding.recyclerView.adapter = null
        viewBinding.recyclerView.adapter = adapter
        adapter.type = sharedPreferencesManager.etaCardType

        when (adapter.type) {
            EtaCardViewType.Classic -> {
                viewBinding.recyclerView.apply {
                    while (itemDecorationCount > 0) {
                        removeItemDecorationAt(0)
                    }
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            LinearLayoutManager.VERTICAL
                        )
                    )
                    setPadding(
                        dpToPx(0),
                        dpToPx(0),
                        dpToPx(0),
                        dpToPx(MainActivity.bottomBarHeight)
                    )
                }
            }
            EtaCardViewType.Standard,
            EtaCardViewType.Compact -> {
                viewBinding.recyclerView.apply {
                    while (itemDecorationCount > 0) {
                        removeItemDecorationAt(0)
                    }
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            LinearLayoutManager.VERTICAL
                        ).apply {
                            setDrawable(
                                ContextCompat.getDrawable(context, R.drawable.space_vertical)!!
                            )
                        }
                    )
                    setPadding(
                        dpToPx(0),
                        dpToPx(4),
                        dpToPx(0),
                        dpToPx(MainActivity.bottomBarHeight)
                    )
                }
            }
        }
    }

    private fun updateAdapterData() {
        Logger.d("updateAdapterData")
        etaCardList = null
        initData()
    }

    private fun hideUpdateEtaFailedText() {
        viewBinding?.updateEtaFailedTv?.apply {
            toggleSlideUp(false)
        }
    }

    override fun onPause() {
        lifecycleScope.launch {
            viewModel.etaRequested.emit(false)
        }
        super.onPause()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentBookmarkHomeBinding.inflate(inflater, container, false)
}
