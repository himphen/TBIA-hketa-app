package hibernate.v2.sunshine.ui.eta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.model.RouteEtaStop
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
        fun getInstance() = EtaFragment()
    }

    private lateinit var adapter: EtaItemAdapter
    private val viewModel by inject<EtaViewModel>()
    private val routeEtaStopList = arrayListOf<RouteEtaStop>()
    private var refreshEtaJob: Deferred<Unit>? = null

    private var routeAndStopListReady = false

    init {
        lifecycleScope.launch {
            launch {
                viewModel.routeEtaStopList.observe(this@EtaFragment, {
                    processEtaList(it)
                })
            }
            launch {
                viewModel.routeAndStopListReady.observe(this@EtaFragment, {
                    routeAndStopListReady = it
                    getRouteEtaStopList()
                })
            }
            launch {
                viewModel.etaEntityList.observe(this@EtaFragment, {
                    viewModel.getRouteAndStopList()
                })
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.getEtaListFromDb()
        }
    }

    private fun getRouteEtaStopList() {
        if (routeAndStopListReady) {
            refreshEtaJob =
                CoroutineScope(Dispatchers.IO).launchPeriodicAsync(REFRESH_TIME) {
                    viewModel.getRouteEtaStopList()
                }
        } else {
            refreshEtaJob?.cancel()
            refreshEtaJob = null
        }
    }

    private fun processEtaList(list: List<RouteEtaStop>) {
        list.forEach { routeEtaStop ->
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

        list.filter { routeEtaStop ->
            routeEtaStop.etaList.isNotEmpty()
        }

        routeEtaStopList.clear()
        routeEtaStopList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EtaItemAdapter(requireContext())
        viewBinding!!.rvlist.adapter = adapter
        adapter.setData(routeEtaStopList)
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
    }
}