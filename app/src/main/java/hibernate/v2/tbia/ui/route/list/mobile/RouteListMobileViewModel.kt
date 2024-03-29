package hibernate.v2.tbia.ui.route.list.mobile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
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
import hibernate.v2.tbia.ui.base.BaseViewModel
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.EnumMap

class RouteListMobileViewModel(
    private val kmbInteractor: KmbInteractor,
    private val ctbInteractor: CtbInteractor,
    private val gmbInteractor: GmbInteractor,
    private val mtrInteractor: MtrInteractor,
    private val lrtInteractor: LrtInteractor,
    private val nlbRepository: NlbInteractor,
) : BaseViewModel() {

    val filteredTransportRouteList = MutableSharedFlow<Pair<EtaType, List<TransportRoute>>>()
    val tabItemSelectedLiveData = MutableSharedFlow<EtaType>()

    var searchRouteKeyword: String = ""

    private var executingSearchJob: EnumMap<EtaType, Job?> = EnumMap(EtaType::class.java)

    fun getTransportRouteList(context: Context, etaType: EtaType) {
        viewModelScope.launch(Dispatchers.IO) {
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

            searchRoute(context, etaType)
        }
    }

    private fun getKmbRouteList() {
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

            Logger.e(e, "lifecycle getKmbRouteList error")
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

            Logger.e(e, "lifecycle getCtbRouteList error")
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

            Logger.e(e, "lifecycle getGmbRouteList error")
        }
    }

    private fun getMTRRouteList() {
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

            Logger.e(e, "lifecycle getMTRRouteList error")
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

            Logger.e(e, "lifecycle getLRTRouteList error")
        }
    }

    private fun getNlbRouteList() {
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

            Logger.e(e, "lifecycle getNLBRouteList error")
        }
    }

    fun searchRoute(context: Context, etaType: EtaType) {
        executingSearchJob[etaType]?.cancel()

        if (!RouteListDataHolder.hasData(etaType)) {
            executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, emptyList()))
            }
            return
        }

        val keyword = searchRouteKeyword
        Logger.d(String.format("Search text changed: %s", keyword))

        val allTransportRouteList = RouteListDataHolder.getData(etaType)!!

        if (keyword.isBlank()) {
            executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, allTransportRouteList))
            }
            return
        }

        executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
            val result = allTransportRouteList.filter { transportRoute ->
                transportRoute.routeNo.startsWith(keyword, true) ||
                    transportRoute.getLocalisedDest(context).startsWith(keyword, true) ||
                    transportRoute.getLocalisedOrig(context).startsWith(keyword, true)
            }

            filteredTransportRouteList.emit(Pair(etaType, result))
        }
    }
}
