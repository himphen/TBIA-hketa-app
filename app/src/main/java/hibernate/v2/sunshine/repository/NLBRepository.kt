package hibernate.v2.sunshine.repository

import com.fonfon.kgeohash.GeoHash
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.sunshine.api.DataServiceProvider
import hibernate.v2.sunshine.db.nlb.NLBDao
import hibernate.v2.sunshine.db.nlb.NLBRouteEntity
import hibernate.v2.sunshine.db.nlb.NLBRouteStopEntity
import hibernate.v2.sunshine.db.nlb.NLBStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.route.TransportRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class NLBRepository(
    private val dao: NLBDao,
    private val dataServiceProvider: DataServiceProvider
) : BaseRepository() {

    suspend fun saveData() {
        val result = ApiSafeCall { dataServiceProvider.getService().getNlbData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    data.route?.let { list ->
                        val temp = list
                            .map(NLBRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(NLBRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("NLBRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { NLBRouteStop ->
                                NLBRouteStopEntity.fromApiModel(NLBRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NLBRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { NLBStop ->
                                NLBStopEntity.fromApiModel(NLBStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NLBRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    suspend fun getStopListDb(list: List<GeoHash>) = dao.getStopList(list.map { it.toString() })
    suspend fun getRouteListDb() = dao.getRouteList()
    suspend fun getRouteStopComponentListDb() = dao.getRouteStopComponentList()
    suspend fun getRouteStopComponentListDb(
        route: TransportRoute,
    ) = dao.getRouteStopComponentList(
        route.routeId
    )

    suspend fun setMapRouteListIntoMapStop(stopList: List<SearchMapStop>): List<SearchMapStop> {
        return stopList.map {
            if (it.mapRouteList.isEmpty()) {
                it.mapRouteList = getRouteEtaCardList(it)
            }
            it
        }
    }

    suspend fun getRouteEtaCardList(stop: SearchMapStop): List<Card.EtaCard> {
        val routeStopList = dao.getRouteStopListFromStopId(stop.stopId)
        val routeList =
            dao.getRouteListFromRouteId(routeStopList).filter { !it.isSpecialRoute() }
        val routeHashMap = routeList.map {
            it.routeHashId() to it
        }.toMap()

        return routeStopList.mapNotNull {
            val route = routeHashMap[it.routeHashId()] ?: return@mapNotNull null

            Card.EtaCard(
                route.toTransportModel(),
                stop.toTransportModelWithSeq(it.seq),
                position = 0
            )
        }
    }

    suspend fun getStopDb(stop: String): NLBStopEntity? = dao.getStop(stop).firstOrNull()

    suspend fun getRouteDb(
        route: String
    ): NLBRouteEntity? = dao.getRoute(
        route
    )

    suspend fun initDatabase() {
        dao.clearRouteList()
        dao.clearStopList()
        dao.clearRouteStopList()
    }

    suspend fun saveRouteList(entityList: List<NLBRouteEntity>) {
        dao.addRouteList(entityList)
    }

    suspend fun saveRouteStopList(entityList: List<NLBRouteStopEntity>) {
        dao.addRouteStopList(entityList)
    }

    suspend fun saveStopList(entityList: List<NLBStopEntity>) {
        dao.addStopList(entityList)
    }
}
