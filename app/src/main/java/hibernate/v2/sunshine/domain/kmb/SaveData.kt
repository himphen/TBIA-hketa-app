package hibernate.v2.sunshine.domain.kmb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getKmbData() }

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
                            .map(KmbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(KmbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { kmbRouteStop ->
                                KmbRouteStopEntity.fromApiModel(kmbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { kmbStop ->
                                KmbStopEntity.fromApiModel(kmbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("KmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<KmbRouteEntity>) {
        kmbDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<KmbRouteStopEntity>) {
        kmbDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<KmbStopEntity>) {
        kmbDao.addStopList(entityList)
    }
}
