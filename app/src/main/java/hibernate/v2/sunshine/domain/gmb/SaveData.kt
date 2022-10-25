package hibernate.v2.sunshine.domain.gmb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.db.gmb.GmbRouteEntity
import hibernate.v2.sunshine.db.gmb.GmbRouteStopEntity
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getGmbData() }

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
                            .map(GmbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(GmbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { gmbRouteStop ->
                                GmbRouteStopEntity.fromApiModel(gmbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { gmbStop ->
                                GmbStopEntity.fromApiModel(gmbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("GmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<GmbRouteEntity>) {
        gmbDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<GmbRouteStopEntity>) {
        gmbDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<GmbStopEntity>) {
        gmbDao.addStopList(entityList)
    }
}
