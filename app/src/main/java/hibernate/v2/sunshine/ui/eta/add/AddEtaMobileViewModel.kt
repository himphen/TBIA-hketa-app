package hibernate.v2.sunshine.ui.eta.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.RouteListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.EnumMap

class AddEtaMobileViewModel(
    private val etaRepository: EtaRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
) : BaseViewModel() {

    val filteredTransportRouteList = MutableSharedFlow<Pair<EtaType, List<TransportRoute>>>()
    val stopList = MutableSharedFlow<List<TransportStop>>()
    var selectedEtaType = MutableLiveData(EtaType.KMB)
    var selectedRoute = MutableLiveData<TransportRoute?>()
    var isAddEtaSuccessful = MutableSharedFlow<Boolean>()

    val searchRouteKeyword = MutableLiveData<String>()

    private var executingSearchJob: EnumMap<EtaType, Job?> = EnumMap(EtaType::class.java)

    private suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company,
    ) = withContext(Dispatchers.IO) {
        etaRepository.hasEtaInDb(
            stopId,
            routeId,
            bound,
            serviceType,
            seq,
            company
        )
    }

    private suspend fun addEta(item: SavedEtaEntity) =
        withContext(Dispatchers.IO) { etaRepository.addEta(item) }

    private suspend fun getEtaOrderList() =
        withContext(Dispatchers.IO) { etaRepository.getEtaOrderList() }

    private suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) =
        withContext(Dispatchers.IO) { etaRepository.updateEtaOrderList(entityList) }

    fun getTransportRouteList(etaType: EtaType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (etaType) {
                EtaType.KMB -> getKmbRouteList()
                EtaType.NWFB,
                EtaType.CTB -> getNCRouteList(etaType)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> getGmbRouteList(etaType)
                EtaType.MTR -> getMTRRouteList()
                EtaType.LRT -> getLRTRouteList()
            }

            searchRoute(etaType)
        }
    }

    fun getTransportStopList() {
        viewModelScope.launch(Dispatchers.IO) {
            val route = selectedRoute.value ?: return@launch

            val list = when (selectedEtaType.value!!) {
                EtaType.KMB -> getKmbStopList(route)
                EtaType.NWFB,
                EtaType.CTB -> getNCStopList(route)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> getGmbStopList(route)
                EtaType.MTR -> getMTRStopList(route)
                EtaType.LRT -> getLRTStopList(route)
                EtaType.NLB -> TODO()
            }

            stopList.emit(list)
        }
    }

    private suspend fun getKmbRouteList() {
        val etaType = EtaType.KMB
        if (RouteListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = kmbRepository.getRouteListDb()
                .filter { !it.isSpecialRoute() }
                .map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList.toMutableList())

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getNCRouteList(etaType: EtaType) {
        if (RouteListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = ncRepository.getRouteListByCompanyDb(etaType.company())
                .map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList.toMutableList())

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getGmbRouteList(etaType: EtaType) {
        if (RouteListDataHolder.hasData(etaType)) {
            return
        }

        val region = when (etaType) {
            EtaType.GMB_HKI -> GmbRegion.HKI
            EtaType.GMB_KLN -> GmbRegion.KLN
            EtaType.GMB_NT -> GmbRegion.NT
            else -> return
        }

        try {
            val allRouteList = gmbRepository.getRouteListDb(region).map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList.toMutableList())

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getMTRRouteList() {
        val etaType = EtaType.MTR
        if (RouteListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = mtrRepository.getRouteEnabledListDb().map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList.toMutableList())

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getLRTRouteList() {
        val etaType = EtaType.LRT
        if (RouteListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = lrtRepository.getRouteEnabledListDb().map { it.toTransportModel() }
            RouteListDataHolder.setData(etaType, allRouteList.toMutableList())

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getKmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = kmbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getKmbStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getKmbStopList error")
            mutableListOf()
        }
    }

    private suspend fun getNCStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = ncRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNCStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNCStopList error")
            mutableListOf()
        }
    }

    private suspend fun getGmbStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = gmbRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNCStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNCStopList error")
            mutableListOf()
        }
    }

    private suspend fun getMTRStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = mtrRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNCStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNCStopList error")
            mutableListOf()
        }
    }

    private suspend fun getLRTStopList(route: TransportRoute): List<TransportStop> {
        return try {
            val allRouteList = lrtRepository.getRouteStopComponentListDb(route)
                .mapNotNull { it.stopEntity?.toTransportModelWithSeq(it.routeStopEntity.seq) }
            Logger.d("lifecycle getNCStopList done")
            allRouteList
        } catch (e: Exception) {
            Logger.e(e, "lifecycle getNCStopList error")
            mutableListOf()
        }
    }

    fun saveStop(card: Card.RouteStopAddCard) {
        viewModelScope.launch(Dispatchers.IO) {
            val isExisting = hasEtaInDb(
                stopId = card.stop.stopId,
                routeId = card.route.routeId,
                bound = card.route.bound,
                serviceType = card.route.serviceType,
                seq = card.stop.seq!!,
                company = card.route.company
            )

            if (isExisting) {
                isAddEtaSuccessful.emit(false)
                return@launch
            }

            val newEta = SavedEtaEntity(
                stopId = card.stop.stopId,
                routeId = card.route.routeId,
                bound = card.route.bound,
                serviceType = card.route.serviceType,
                seq = card.stop.seq!!,
                company = card.route.company
            )
            addEta(newEta)

            val currentEtaOrderList = getEtaOrderList()
            val updatedEtaOrderList = mutableListOf<EtaOrderEntity>()
            updatedEtaOrderList.add(EtaOrderEntity(id = newEta.id, position = 0))
            updatedEtaOrderList.addAll(currentEtaOrderList.map {
                EtaOrderEntity(id = it.id, position = it.position + 1)
            })
            updateEtaOrderList(updatedEtaOrderList)

            isAddEtaSuccessful.emit(true)
        }
    }

    fun searchRoute(etaType: EtaType) {
        executingSearchJob[etaType]?.cancel()

        if (!RouteListDataHolder.hasData(etaType)) {
            executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, emptyList()))
            }
            return
        }

        val keyword = searchRouteKeyword.value
        Logger.d(String.format("Search text changed: %s", keyword))

        val allTransportRouteList = RouteListDataHolder.getData(etaType)!!

        if (keyword.isNullOrBlank()) {
            executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, allTransportRouteList))
            }
            return
        }

        executingSearchJob[etaType] = viewModelScope.launch(Dispatchers.IO) {
            val result = allTransportRouteList.filter { transportRoute ->
                transportRoute.routeNo.startsWith(keyword, true)
                        || transportRoute.destTc.startsWith(keyword, true)
                        || transportRoute.origTc.startsWith(keyword, true)
            }

            filteredTransportRouteList.emit(Pair(etaType, result))
        }
    }
}
