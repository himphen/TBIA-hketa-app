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
import com.himphen.logger.Logger
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

class EtaGridFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_NONE

        private const val REFRESH_TIME = 30 * 1000L
        fun getInstance() = EtaGridFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null

    private val viewModel by inject<EtaViewModel>()
    private var routeEtaStopList = arrayListOf<Card.RouteEtaStopCard>()
    private var refreshEtaJob: Deferred<Unit>? = null

    init {
        lifecycleScope.launch {
            launch {
                viewModel.routeEtaStopList.observe(this@EtaGridFragment, {
                    routeEtaStopList.clear()
                    routeEtaStopList.addAll(it)
                    mAdapter?.clear()
                    mAdapter?.addAll(0, routeEtaStopList)
                    processEtaList()
                })
            }
            launch {
                viewModel.etaEntityList.observe(this@EtaGridFragment, {
                    viewModel.initRouteEtaStopList()
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

        val gridPresenter = FullWidthGridPresenter(ZOOM_FACTOR)
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
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.Theme_Fragment_Eta)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return super.onCreateView(localInflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val cardPresenter = EtaCardPresenter(requireContext(), getFragmentWidth())
            mAdapter = ArrayObjectAdapter(cardPresenter)
            mAdapter?.addAll(0, routeEtaStopList)
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
                if (routeEtaStopList.isNotEmpty()) {
                    viewModel.updateRouteEtaStopList()
                }
            }
        } else {
            refreshEtaJob?.cancel()
            refreshEtaJob = null
        }
    }

    private fun processEtaList() {
        routeEtaStopList.forEach { routeEtaStop ->
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
        }

//        list.filter { routeEtaStop ->
//            routeEtaStop.etaList.isNotEmpty()
//        }

        mAdapter?.notifyItemRangeChanged(0, routeEtaStopList.size)
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
    }

    private suspend fun getFragmentWidth(): Int {
        return suspendCoroutine { continuation ->
            view?.viewTreeObserver?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val width = view?.width ?: -1
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    Logger.d("width: " + width)
                    continuation.resume(width)
                }
            })
        }
    }
}