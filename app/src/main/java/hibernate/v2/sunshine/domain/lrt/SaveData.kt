package hibernate.v2.sunshine.domain.lrt

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.service.DataService
import hibernate.v2.sunshine.db.lrt.LrtDao
import hibernate.v2.sunshine.db.lrt.LrtRouteEntity
import hibernate.v2.sunshine.db.lrt.LrtRouteStopEntity
import hibernate.v2.sunshine.db.lrt.LrtStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataService.getLrtData() }

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
                            .map(LrtRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(LrtRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { routeStop ->
                                LrtRouteStopEntity.fromApiModel(routeStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { stop ->
                                LrtStopEntity.fromApiModel(stop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<LrtRouteEntity>) {
        lrtDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<LrtRouteStopEntity>) {
        lrtDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<LrtStopEntity>) {
        lrtDao.addStopList(entityList)
    }
}
