package hibernate.v2.sunshine.ui.route.list.leanback

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.sunshine.model.AddEtaRowItem
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.GmbTransportRoute
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.model.transport.TransportRouteStopList
import hibernate.v2.sunshine.repository.CtbRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RouteListLeanbackViewModel(
    private val kmbRepository: KmbRepository,
    private val ctbRepository: CtbRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val filteredTransportRouteList = MutableSharedFlow<Pair<EtaType, List<AddEtaRowItem>>>()

    val searchRouteKeyword = MutableLiveData<String>()

    private var executingSearchJob: Job? = null

    fun getTransportRouteList(context: Context, etaType: EtaType) {
        viewModelScope.launch(Dispatchers.IO) {
            when (etaType) {
                EtaType.KMB -> getKmbRouteList(context)
                EtaType.NWFB,
                EtaType.CTB -> getCtbouteList(context, etaType)
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> getGmbRouteList(context, etaType)
                EtaType.MTR -> getMTRRouteList(context)
                EtaType.LRT -> getLRTRouteList(context)
                EtaType.NLB -> getNLBRouteList(context)
            }

            searchRoute(etaType)
        }
    }

    fun searchRoute(etaType: EtaType) {
        executingSearchJob?.cancel()
        if (!RouteAndStopListDataHolder.hasData(etaType)) {
            executingSearchJob = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, emptyList()))
            }
            return
        }

        val keyword = searchRouteKeyword.value
        Logger.d(String.format("Search text changed: %s", keyword))

        val allTransportRouteList = RouteAndStopListDataHolder.getData(etaType)!!

        if (keyword.isNullOrBlank()) {
            executingSearchJob = viewModelScope.launch(Dispatchers.IO) {
                filteredTransportRouteList.emit(Pair(etaType, allTransportRouteList))
            }
            return
        }

        executingSearchJob = viewModelScope.launch(Dispatchers.IO) {
            val result = allTransportRouteList.filter { routeForRowAdapter ->
                routeForRowAdapter.route.routeNo.startsWith(keyword, true) ||
                    routeForRowAdapter.route.destTc.startsWith(keyword, true) ||
                    routeForRowAdapter.route.origTc.startsWith(keyword, true)
            }

            filteredTransportRouteList.emit(Pair(etaType, result))
        }
    }

    private suspend fun getKmbRouteList(context: Context) {
        val etaType = EtaType.KMB
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = kmbRepository.getRouteListDb().filter { !it.isSpecialRoute() }
            val allRouteStopList = kmbRepository.getRouteStopComponentListDb()

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

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    val headerTitle = route.getDirectionWithRouteText(context)

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(etaType, transportRouteList)

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getCtbouteList(context: Context, etaType: EtaType) {
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = ctbRepository.getRouteListByCompanyDb(etaType.company())
            val allRouteStopList = ctbRepository.getRouteStopComponentListDb(etaType.company())

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

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    val headerTitle = route.getDirectionWithRouteText(context)

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getGmbRouteList(context: Context, etaType: EtaType) {
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        val region = when (etaType) {
            EtaType.GMB_HKI -> GmbRegion.HKI
            EtaType.GMB_KLN -> GmbRegion.KLN
            EtaType.GMB_NT -> GmbRegion.NT
            else -> return
        }

        try {
            val allRouteList = gmbRepository.getRouteListDb(region)
            val allRouteStopList =
                gmbRepository.getRouteStopComponentListDb(allRouteList.map { it.routeId })

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

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getMTRRouteList(context: Context) {
        val etaType = EtaType.MTR
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = mtrRepository.getRouteEnabledListDb()
            val allRouteStopList = mtrRepository.getRouteStopComponentListDb()

            val transportRouteStopHashMap = allRouteList.associate { entity ->
                val route = entity.toTransportModel()
                route.routeHashId() to TransportRouteStopList(
                    route = route,
                    stopList = mutableListOf()
                )
            }

            Logger.d(allRouteStopList)

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

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    route as MTRTransportRoute
                    val headerTitle = route.getDirectionWithRouteText(context)

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            Logger.d(transportRouteStopHashMap)
            Logger.d(transportRouteList)

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getLRTRouteList(context: Context) {
        val etaType = EtaType.LRT
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = lrtRepository.getRouteEnabledListDb()
            val allRouteStopList = lrtRepository.getRouteStopComponentListDb()

            val transportRouteStopHashMap = allRouteList.associate { entity ->
                val route = entity.toTransportModel()
                route.routeHashId() to TransportRouteStopList(
                    route = route,
                    stopList = mutableListOf()
                )
            }

            Logger.d(allRouteStopList)

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

            val transportRouteList =
                transportRouteStopList.map { routeAndStopList ->
                    val route = routeAndStopList.route
                    route as LRTTransportRoute
                    val headerTitle = route.getDirectionWithRouteText(context)

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            Logger.d(transportRouteStopHashMap)
            Logger.d(transportRouteList)

            RouteAndStopListDataHolder.setData(
                etaType,
                transportRouteList
            )

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }

    private suspend fun getNLBRouteList(context: Context) {
        val etaType = EtaType.NLB
        if (RouteAndStopListDataHolder.hasData(etaType)) {
            return
        }

        try {
            val allRouteList = nlbRepository.getRouteListDb().filter { !it.isSpecialRoute() }
            val allRouteStopList = nlbRepository.getRouteStopComponentListDb()

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

                    AddEtaRowItem(
                        headerTitle = headerTitle,
                        route = route,
                        filteredList = routeAndStopList.stopList
                    )
                }.toMutableList()

            RouteAndStopListDataHolder.setData(etaType, transportRouteList)

            Logger.t("lifecycle").d("getTransportRouteList done")
        } catch (e: Exception) {
            RouteAndStopListDataHolder.setData(etaType, mutableListOf())

            Logger.e(e, "lifecycle getTransportRouteList error")
        }
    }
}
