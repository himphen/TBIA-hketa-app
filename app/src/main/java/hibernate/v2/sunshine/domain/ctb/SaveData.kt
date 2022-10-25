package hibernate.v2.sunshine.domain.ctb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.sunshine.db.ctb.CtbDao
import hibernate.v2.sunshine.db.ctb.CtbRouteEntity
import hibernate.v2.sunshine.db.ctb.CtbRouteStopEntity
import hibernate.v2.sunshine.db.ctb.CtbStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getCtbData() }

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
                            .map(CtbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(CtbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { ctbRouteStop ->
                                CtbRouteStopEntity.fromApiModel(ctbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { ctbStop ->
                                CtbStopEntity.fromApiModel(ctbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NCRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<CtbRouteEntity>) {
        ctbDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<CtbRouteStopEntity>) {
        ctbDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<CtbStopEntity>) {
        ctbDao.addStopList(entityList)
    }
}
