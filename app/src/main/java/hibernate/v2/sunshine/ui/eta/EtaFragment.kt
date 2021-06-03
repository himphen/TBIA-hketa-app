package hibernate.v2.sunshine.ui.eta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.model.RouteEtaStop
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

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
    private val etaEntityList = arrayListOf<EtaEntity>()
    private var refreshEtaJob: Deferred<Unit>? = null

    init {
        lifecycleScope.launch {
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
                                viewModel.getEtaList(etaEntityList)
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
        etaEntityList.add(
            EtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290",
                bound = Bound.OUTBOUND.full,
                serviceType = "1"
            )
        )
        etaEntityList.add(
            EtaEntity(
                stopId = "9D208FE6B2CFD450",
                routeId = "290X",
                bound = Bound.OUTBOUND.full,
                serviceType = "1"
            )
        )
        etaEntityList.add(
            EtaEntity(
                stopId = "403881982F9E7209",
                routeId = "296A",
                bound = Bound.OUTBOUND.full,
                serviceType = "1"
            )
        )
        etaEntityList.add(
            EtaEntity(
                stopId = "5527FF8CC85CF139",
                routeId = "296C",
                bound = Bound.OUTBOUND.full,
                serviceType = "1"
            )
        )
        etaEntityList.add(
            EtaEntity(
                stopId = "21E3E95EAEB2048C",
                routeId = "296D",
                bound = Bound.OUTBOUND.full,
                serviceType = "1"
            )
        )
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRouteAndStopList(etaEntityList)
    }
}