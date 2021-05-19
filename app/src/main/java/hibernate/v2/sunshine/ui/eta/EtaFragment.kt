package hibernate.v2.sunshine.ui.eta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.api.model.RouteEtaStop
import hibernate.v2.api.request.EtaRequest
import hibernate.v2.api.request.RouteRequest
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Date

class EtaFragment : BaseFragment<FragmentEtaBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEtaBinding.inflate(inflater, container, false)

    companion object {
        private const val REFRESH_TIME = 60L * 1000
    }

    private lateinit var adapter: EtaItemAdapter
    private val viewModel by inject<EtaViewModel>()
    private val routeEtaStopList = arrayListOf<RouteEtaStop>()
    private val etaRequests = arrayListOf<EtaRequest>()
    private var refreshEtaJob: Deferred<Unit>? = null

    init {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.routeEtaStopList.observe(this@EtaFragment, {
                    processEtaList(it)
                })
            }
            launch {
                viewModel.routeAndStopListReady.observe(this@EtaFragment, {
                    if (it) {
                        refreshEtaJob =
                            CoroutineScope(Dispatchers.IO).launchPeriodicAsync(REFRESH_TIME) {
                                viewModel.getEtaList(etaRequests)
                            }
                    } else {
                        refreshEtaJob?.cancel()
                        refreshEtaJob = null
                    }
                })
            }
        }
    }

    private fun processEtaList(list: List<RouteEtaStop>) {
        val updatedList = list.sortedBy { item -> item.route.route }
        updatedList.forEach { routeEtaStop ->
            routeEtaStop.etaList = routeEtaStop.etaList.filter { eta ->
                DateUtil.getTimeDiffInMin(
                    DateUtil.getDate(
                        eta.eta,
                        DateFormat.ISO_WITHOUT_MS.value
                    ) ?: Date(),
                    DateUtil.getDate(
                        eta.dataTimestamp,
                        DateFormat.ISO_WITHOUT_MS.value
                    ) ?: Date()
                ) > 0
            }
        }

        updatedList.filter { routeEtaStop ->
            routeEtaStop.etaList.isNotEmpty()
        }

        routeEtaStopList.clear()
        routeEtaStopList.addAll(updatedList)
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EtaItemAdapter()
        viewBinding!!.rvlist.adapter = adapter
        adapter.setData(routeEtaStopList)

        initData()
    }

    private fun initData() {
        etaRequests.add(
            EtaRequest(
                "9D208FE6B2CFD450",
                RouteRequest(routeId = "290", bound = RouteRequest.Bound.OUTBOUND)
            )
        )
        etaRequests.add(
            EtaRequest(
                "9D208FE6B2CFD450",
                RouteRequest(routeId = "290A", bound = RouteRequest.Bound.OUTBOUND)
            )
        )
        etaRequests.add(
            EtaRequest(
                "9D208FE6B2CFD450",
                RouteRequest(routeId = "290X", bound = RouteRequest.Bound.OUTBOUND)
            )
        )
        viewModel.getRouteAndStopList(etaRequests)
    }
}