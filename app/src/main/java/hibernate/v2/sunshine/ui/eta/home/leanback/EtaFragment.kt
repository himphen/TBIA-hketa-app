package hibernate.v2.sunshine.ui.eta.home.leanback

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.LbFragmentEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.FullWidthGridPresenter
import hibernate.v2.sunshine.ui.eta.home.EtaViewModel
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.launchPeriodicAsync
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * TODO: auto scroll feature using setSelectedPosition
 */
class EtaFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_NONE

        fun getInstance() = EtaFragment()
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private var mAdapter: ArrayObjectAdapter? = null

    private val viewModel by inject<EtaViewModel>()
    private var etaCardList: MutableList<Card.EtaCard>? = null
    private var refreshEtaJob: Deferred<Unit>? = null

    var rootViewBinding: LbFragmentEtaBinding? = null

    override fun onResume() {
        super.onResume()
        updateRouteEtaStopList()
    }

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
            etaCardList?.let { etaCardList ->
                etaCardList.clear()
                etaCardList.addAll(it)
                processEtaList()
            } ?: run {
                etaCardList = it.toMutableList()
                mAdapter?.addAll(0, etaCardList)
                processEtaList()

                viewModel.updateEtaList(etaCardList)
            }
        }
        viewModel.etaUpdateError.onEach {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
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
        val cardPresenter = EtaCardPresenter(
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

    private fun updateRouteEtaStopList() {
        if (refreshEtaJob == null) {
            refreshEtaJob =
                CoroutineScope(Dispatchers.Main).launchPeriodicAsync(EtaViewModel.REFRESH_TIME) {
                    viewModel.updateEtaList(etaCardList)
                }
        }
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

                mAdapter?.replace(index, etaCard)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
        refreshEtaJob = null
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
}