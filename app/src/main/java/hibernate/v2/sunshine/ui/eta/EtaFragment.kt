package hibernate.v2.sunshine.ui.eta

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.lifecycle.lifecycleScope
import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.FullWidthGridPresenter
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * TODO: auto scroll feature using setSelectedPosition
 */
class EtaFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_NONE

        private const val REFRESH_TIME = 60 * 1000L
        fun getInstance() = EtaFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null

    private val viewModel by inject<EtaViewModel>()
    private var routeEtaStopList: MutableList<Card.RouteEtaStopCard>? = null
    private var refreshEtaJob: Deferred<Unit>? = null

    init {
        lifecycleScope.launch {
            launch {
                viewModel.routeEtaStopList.observe(this@EtaFragment, {
                    routeEtaStopList?.let { routeEtaStopList ->
                        routeEtaStopList.clear()
                        routeEtaStopList.addAll(it)
                        processEtaList()
                    } ?: run {
                        routeEtaStopList = it.toMutableList()
                        mAdapter?.addAll(0, routeEtaStopList)
                        processEtaList()

                        viewModel.updateRouteEtaStopList()
                    }
                })
            }
        }
    }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val customInflater = inflater.cloneInContext(
            ContextThemeWrapper(
                context,
                R.style.Theme_Fragment_Eta
            )
        )
        return super.onCreateView(customInflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val cardPresenter = EtaCardPresenter(requireContext(), getFragmentWidth(view))
            mAdapter = ArrayObjectAdapter(cardPresenter)
            adapter = mAdapter

            viewModel.getEtaListFromDb()

            prepareEntranceTransition()
            Handler(Looper.getMainLooper()).postDelayed({
                startEntranceTransition()
            }, 1000)
        }
    }

    private fun updateRouteEtaStopList() {
        if (refreshEtaJob == null) {
            refreshEtaJob = CoroutineScope(Dispatchers.IO).launchPeriodicAsync(REFRESH_TIME) {
                if (routeEtaStopList?.isNotEmpty() == true) {
                    viewModel.updateRouteEtaStopList()
                }
            }
        }
    }

    private fun processEtaList() {
        routeEtaStopList?.let { routeEtaStopList ->
            routeEtaStopList.forEachIndexed { index, routeEtaStop ->
                routeEtaStop.etaList = routeEtaStop.etaList.filter { eta: Eta ->
                    eta.eta?.let { etaString ->
                        val etaDate = DateUtil.getDate(
                            etaString,
                            DateFormat.ISO_WITHOUT_MS.value
                        )

                        val currentDate = DateUtil.getDate(
                            eta.dataTimestamp,
                            DateFormat.ISO_WITHOUT_MS.value
                        )

                        DateUtil.getTimeDiffInMin(
                            etaDate!!,
                            currentDate!!
                        ) > 0
                    } ?: run {
                        false
                    }
                }

                mAdapter?.replace(index, routeEtaStop)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
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