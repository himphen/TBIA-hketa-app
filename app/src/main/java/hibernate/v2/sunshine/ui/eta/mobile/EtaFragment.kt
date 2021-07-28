package hibernate.v2.sunshine.ui.eta.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.himphen.logger.Logger
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainFragment
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Date

class EtaFragment : BaseFragment<FragmentEtaBinding>() {

    companion object {
        private const val REFRESH_TIME = 60 * 1000L
        fun getInstance() = EtaFragment()
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val viewModel by inject<EtaViewModel>()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private val adapter = EtaCardAdapter(sharedPreferencesManager.etaCardType)

    private var etaCardList: MutableList<Card.EtaCard>? = null
    private var refreshEtaJob: Deferred<Unit>? = null

    override fun onResume() {
        super.onResume()
        updateRouteEtaStopList()
    }

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            etaCardList?.let { etaCardList ->
                etaCardList.clear()
                etaCardList.addAll(it)
                processEtaList()
            } ?: run {
                etaCardList = it.toMutableList()
                adapter.setData(etaCardList)
                processEtaList()

                viewModel.updateEtaList(etaCardList)
            }
        }

        mainViewModel.onUpdatedEtaList.onEach {
            updateAdapterData()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        mainViewModel.onUpdatedEtaList.onEach {
            initAdapter()
            updateAdapterData()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initUi()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.getEtaListFromDb()
        }
    }

    private fun initUi() {
        initAdapter()
    }

    private fun updateRouteEtaStopList() {
        if (refreshEtaJob == null) {
            refreshEtaJob = CoroutineScope(Dispatchers.Main).launchPeriodicAsync(REFRESH_TIME) {
                viewModel.updateEtaList(etaCardList)
            }
        }
    }

    private fun processEtaList() {
        val etaCardList = etaCardList
        if (etaCardList.isNullOrEmpty()) {
            viewBinding?.emptyViewCl?.root?.visibility = View.VISIBLE
            viewBinding?.emptyViewCl?.emptyDescTv?.text = getString(R.string.empty_eta_list)
        } else {
            viewBinding?.emptyViewCl?.root?.visibility = View.GONE
            etaCardList.forEachIndexed { index, etaCard ->
                etaCard.etaList = etaCard.etaList.filter { eta: TransportEta ->
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
                        dpToPx(MainFragment.bottomBarHeight)
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
                        })

                    setPadding(
                        dpToPx(0),
                        dpToPx(4),
                        dpToPx(0),
                        dpToPx(MainFragment.bottomBarHeight)
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

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEtaBinding.inflate(inflater, container, false)
}