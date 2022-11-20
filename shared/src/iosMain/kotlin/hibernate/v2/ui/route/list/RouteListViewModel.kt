package hibernate.v2.ui.route.list

import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.lrt.LrtInteractor
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.model.dataholder.RouteListDataHolder
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.utils.CommonLogger
import hibernate.v2.utils.IOSContext
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RouteListViewModel(
    private val filteredTransportRouteList: (EtaType, List<TransportRoute>) -> Unit
) : KoinComponent {

    private val kmbInteractor: KmbInteractor by inject()
    private val ctbInteractor: CtbInteractor by inject()
    private val gmbInteractor: GmbInteractor by inject()
    private val mtrInteractor: MtrInteractor by inject()
    private val lrtInteractor: LrtInteractor by inject()
    private val nlbRepository: NlbInteractor by inject()

    var searchRouteKeyword: String = ""

    suspend fun getTransportRouteList(etaType: EtaType) {
        withContext(Dispatchers.Default) {
            when (etaType) {
                EtaType.KMB -> getKmbRouteList()
                EtaType.NWFB,
                EtaType.CTB -> getCtbRouteList(etaType)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> getGmbRouteList(etaType)
                EtaType.MTR -> getMTRRouteList()
                EtaType.LRT -> getLRTRouteList()
                EtaType.NLB -> getNlbRouteList()
            }

            searchRoute(etaType)
        }
    }

    private suspend fun getKmbRouteList() {
        val etaType = EtaType.KMB
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getKmbRouteList hasData")
            return
        }

        try {
            val allRouteList = kmbInteractor.getRouteListDb()
                .map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getKmbRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getKmbRouteList error")
        }
    }

    private fun getCtbRouteList(etaType: EtaType) {
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getCtbRouteList hasData")
            return
        }

        try {
            val allRouteList = ctbInteractor.getRouteListDb(etaType.company())
                .map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getCtbRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getCtbRouteList error")
        }
    }

    private fun getGmbRouteList(etaType: EtaType) {
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getGmbRouteList hasData")
            return
        }

        val region = when (etaType) {
            EtaType.GMB_HKI -> GmbRegion.HKI
            EtaType.GMB_KLN -> GmbRegion.KLN
            EtaType.GMB_NT -> GmbRegion.NT
            else -> return
        }

        try {
            val allRouteList = gmbInteractor.getRouteListDb(region).map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getGmbRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getGmbRouteList error")
        }
    }

    private suspend fun getMTRRouteList() {
        val etaType = EtaType.MTR
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getMTRRouteList hasData")
            return
        }

        try {
            val allRouteList = mtrInteractor.getRouteListDb(true).map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getMTRRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getMTRRouteList error")
        }
    }

    private fun getLRTRouteList() {
        val etaType = EtaType.LRT
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getLRTRouteList hasData")
            return
        }

        try {
            val allRouteList = lrtInteractor.getRouteListDb(true).map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getLRTRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getLRTRouteList error")
        }
    }

    private suspend fun getNlbRouteList() {
        val etaType = EtaType.NLB
        if (RouteListDataHolder.hasData(etaType)) {
            logLifecycle("getNLBRouteList hasData")
            return
        }

        try {
            val allRouteList = nlbRepository.getRouteListDb()
                .map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList)

            logLifecycle("getNLBRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, listOf())

            logLifecycle(e, "lifecycle getNLBRouteList error")
        }
    }

    fun searchRoute(etaType: EtaType) {
        if (!RouteListDataHolder.hasData(etaType)) {
            filteredTransportRouteList(etaType, emptyList())
            return
        }

        val keyword = searchRouteKeyword
        CommonLogger.d("Search text changed: $keyword")

        val allTransportRouteList = RouteListDataHolder.getData(etaType)!!

        if (keyword.isBlank()) {
            filteredTransportRouteList(etaType, allTransportRouteList)
            return
        }

        val result = allTransportRouteList.filter { transportRoute ->
            transportRoute.routeNo.startsWith(keyword, true) ||
                transportRoute.getLocalisedDest(IOSContext()).startsWith(keyword, true) ||
                transportRoute.getLocalisedOrig(IOSContext()).startsWith(keyword, true)
        }

        filteredTransportRouteList(etaType, result)
    }

}
