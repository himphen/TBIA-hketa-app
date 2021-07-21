package hibernate.v2.sunshine.ui.settings.eta.add

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.GmbTransportRoute
import hibernate.v2.sunshine.model.transport.TransportRouteStopList
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEtaViewModel(
    private val etaRepository: EtaRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
) : BaseViewModel() {

    val filteredTransportRouteList = MutableSharedFlow<Pair<EtaType, List<RouteForRowAdapter>>>()
    var selectedEtaType = MutableLiveData<EtaType?>()
    var selectedRoute = MutableLiveData<RouteForRowAdapter?>()
    var isAddEtaSuccessful = MutableSharedFlow<Boolean>()

    val searchRouteKeyword = MutableLiveData<String>()

    private var executingSearchJob: Job? = null

    private suspend fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
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

    fun getTransportRouteList(context: Context, etaType: EtaType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (etaType) {
                EtaType.KMB -> {
                    getKmbRouteList(context)
                }
                EtaType.NWFB_CTB -> {
                    getNCRouteList(context)
                }
                EtaType.GMB -> {
                    getGmbRouteList(context)
                }
            }

            searchRoute(etaType)
        }
    }

    private suspend fun getKmbRouteList(context: Context) {
        val etaType = EtaType.KMB
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = kmbRepository.getRouteListDb()
            val allRouteStopList = kmbRepository.getRouteStopDetailsListDb()

            val transportRouteStopHashMap = allRouteList.associate { entity ->
                val route = entity.toTransportModel()
                route.routeHashId() to TransportRouteStopList(
                    route = route,
                    stopList = mutableListOf()
                )
            }

            allRouteStopList.forEach {
                val routeHashId = it.routeStopEntity.routeHashId()
                val stop = it.stopEntity?.toTransportModelWithSeq(
                    it.routeStopEntity.seq
                )
                stop?.let {
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(stop)
                }
            }

            val transportRouteStopList = transportRouteStopHashMap.values.toMutableList()
            transportRouteStopList.sortWith { o1, o2 ->
                o1.compareTo(o2)
            }

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    val headerTitle = route.getDirectionWithRouteText(context)

                    RouteForRowAdapter(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList.map { stop ->
                            Card.RouteStopAddCard(
                                route = route,
                                stop = stop
                            )
                        }
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(etaType, transportRouteList)

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getNCRouteList(context: Context) {
        val etaType = EtaType.NWFB_CTB
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = ncRepository.getRouteListDb()
            val allRouteStopList = ncRepository.getRouteStopDetailsListDb()

            val transportRouteStopHashMap = allRouteList.associate { entity ->
                val route = entity.toTransportModel()
                route.routeHashId() to TransportRouteStopList(
                    route = route,
                    stopList = mutableListOf()
                )
            }

            allRouteStopList.forEach {
                val routeHashId = it.routeStopEntity.routeHashId()
                val stop = it.stopEntity?.toTransportModelWithSeq(
                    it.routeStopEntity.seq
                )
                stop?.let {
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(stop)
                }
            }

            val transportRouteStopList = transportRouteStopHashMap.values.toMutableList()
            transportRouteStopList.sort()

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    val headerTitle = route.getDirectionWithRouteText(context)

                    RouteForRowAdapter(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList.map { stop ->
                            Card.RouteStopAddCard(
                                route = route,
                                stop = stop
                            )
                        }
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getGmbRouteList(context: Context) {
        val etaType = EtaType.GMB
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = gmbRepository.getRouteListDb()
            val allRouteStopList = gmbRepository.getRouteStopDetailsListDb()

            val transportRouteStopHashMap = allRouteList.associate { entity ->
                val route = entity.toTransportModel()
                route.routeHashId() to TransportRouteStopList(
                    route = route,
                    stopList = mutableListOf()
                )
            }

            allRouteStopList.forEach {
                val routeHashId = it.routeStopEntity.routeHashId()
                val stop = it.stopEntity?.toTransportModelWithSeq(
                    it.routeStopEntity.seq
                )
                stop?.let {
                    transportRouteStopHashMap[routeHashId]?.stopList?.add(stop)
                }
            }

            val transportRouteStopList = transportRouteStopHashMap.values.toMutableList()
            transportRouteStopList.sort()

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    route as GmbTransportRoute
                    val headerTitle = route.getDirectionWithRouteText(context)

                    RouteForRowAdapter(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList.map { stop ->
                            Card.RouteStopAddCard(
                                route = route,
                                stop = stop
                            )
                        }
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.d("lifecycle getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
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
        executingSearchJob?.cancel()
        if (!RouteAndStopListDataHolder.hasData(etaType)) {
            executingSearchJob = viewModelScope.launch {
                filteredTransportRouteList.emit(Pair(etaType, emptyList()))
            }
            return
        }

        val keyword = searchRouteKeyword.value
        Logger.d(String.format("Search text changed: %s", keyword))

        val allTransportRouteList = RouteAndStopListDataHolder.getData(etaType)!!

        if (keyword.isNullOrBlank()) {
            executingSearchJob = viewModelScope.launch {
                filteredTransportRouteList.emit(Pair(etaType, allTransportRouteList))
            }
            return
        }

        executingSearchJob = viewModelScope.launch {
            val result = allTransportRouteList.filter { routeForRowAdapter ->
                routeForRowAdapter.route.routeNo.startsWith(keyword, true)
            }

            filteredTransportRouteList.emit(Pair(etaType, result))
        }
    }

    enum class EtaType {
        KMB,
        NWFB_CTB,
        GMB;

        fun etaTypeName(context: Context) = when (this) {
            KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
            NWFB_CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
            GMB -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
        }

        fun etaTypeColors(context: Context): IntArray = when (this) {
            KMB -> intArrayOf(context.getColor(R.color.brand_color_kmb))
            NWFB_CTB -> intArrayOf(
                context.getColor(R.color.brand_color_nwfb),
//                context.getColor(R.color.brand_color_ctb)
            )
            GMB -> intArrayOf(context.getColor(R.color.brand_color_gmb))
        }
    }
}
