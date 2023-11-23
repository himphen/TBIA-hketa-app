package hibernate.v2.tbia.ui.bookmark.home.leanback

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.himphen.logger.Logger
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.model.Card
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.LbFragmentEtaBinding
import hibernate.v2.tbia.ui.base.FullWidthGridPresenter
import hibernate.v2.tbia.ui.bookmark.home.BookmarkHomeViewModel
import hibernate.v2.tbia.util.GeneralUtils
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.tickerFlow
import hibernate.v2.tbia.util.toggleSlideDown
import hibernate.v2.tbia.util.visible
import hibernate.v2.utils.getEtaUpdateErrorMessage
import hibernate.v2.utils.getTimeDiffFromNowInMin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * TODO: auto scroll feature using setSelectedPosition
 */
class BookmarkHomeFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_NONE

        fun getInstance() = BookmarkHomeFragment()
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private var mAdapter: ArrayObjectAdapter? = null

    private val viewModel by inject<BookmarkHomeViewModel>()
    private var etaCardList: MutableList<Card.EtaCard>? = null

    private var etaRequestJob: Job? = null

    private var rootViewBinding: LbFragmentEtaBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gridPresenter = FullWidthGridPresenter(ZOOM_FACTOR, false)
        gridPresenter.keepChildForeground = false
        gridPresenter.numberOfColumns = COLUMNS
        gridPresenter.shadowEnabled = false
        setGridPresenter(gridPresenter)
    }

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            viewModel.lastUpdatedTime.postValue(System.currentTimeMillis())
            hideUpdateEtaFailedText()
            etaCardList?.let { etaCardList ->
                etaCardList.clear()
                etaCardList.addAll(it)
                processEtaList()
            } ?: run {
                val etaCardList = it.toMutableList()
                this.etaCardList = etaCardList
                mAdapter?.addAll(0, etaCardList)
                processEtaList()

                lifecycleScope.launch {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
        viewModel.etaUpdateError.onEach {
            Logger.e(it, "etaUpdateError")
            if (isResumed) {
                rootViewBinding?.updateEtaFailedTv?.apply {
                    toggleSlideDown(true)
                    text = getEtaUpdateErrorMessage(it, requireContext())
                }

                delay(10.toDuration(DurationUnit.SECONDS))
                viewModel.etaRequested.emit(true)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.etaRequested.onEach {
            if (it) {
                etaRequestJob = viewModel.updateEtaList()
            } else {
                etaRequestJob?.cancel()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                tickerFlow(GeneralUtils.ETA_REFRESH_TIME.milliseconds).collect {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val customInflater = inflater.cloneInContext(
            ContextThemeWrapper(
                context,
                R.style.AppTheme_Leanback_Eta
            )
        )
        val view = super.onCreateView(customInflater, container, savedInstanceState)

        rootViewBinding = LbFragmentEtaBinding.inflate(inflater).apply {
            lbContainer.addView(view)
            emptyViewCl.apply {
                gone()
                contentEmptyList.emptyDescTv.text = getString(R.string.empty_eta_list)
            }
        }
        return rootViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        lifecycleScope.launch {
            initUi()
            initData()
        }
    }

    private suspend fun initUi() {
        val cardPresenter = BookmarkHomeEtaCardPresenter(
            requireContext(),
            getFragmentWidth(requireView()),
            sharedPreferencesManager.etaCardType
        )
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
    }

    private fun initData() {
        viewModel.getEtaListFromDb()
    }

    private fun processEtaList() {
        val etaCardList = etaCardList
        if (etaCardList.isNullOrEmpty()) {
            rootViewBinding?.emptyViewCl?.visible()
            rootViewBinding?.lbContainer?.gone()
        } else {
            rootViewBinding?.emptyViewCl?.gone()
            rootViewBinding?.lbContainer?.visible()
            etaCardList.forEachIndexed { index, etaCard ->
                val temp = etaCard.etaList.filter { eta: TransportEta ->
                    eta.eta?.let { etaDate ->
                        getTimeDiffFromNowInMin(etaDate) > 0
                    } ?: run {
                        false
                    }
                }

                etaCard.etaList.clear()
                etaCard.etaList.addAll(temp)

                mAdapter?.replace(index, etaCard)
            }
        }
    }

    override fun onPause() {
        lifecycleScope.launch {
            viewModel.etaRequested.emit(false)
        }
        super.onPause()
    }

    private suspend fun getFragmentWidth(view: View): Int {
        return suspendCoroutine { continuation ->
            view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val width = view.width
                    view.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    continuation.resume(width)
                }
            })
        }
    }

    private fun hideUpdateEtaFailedText() {
        rootViewBinding?.updateEtaFailedTv?.apply {
            toggleSlideDown(false)
        }
    }
}
