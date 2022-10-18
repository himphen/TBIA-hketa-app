package hibernate.v2.sunshine.domain.nlb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.service.DataService
import hibernate.v2.sunshine.db.nlb.NlbDao
import hibernate.v2.sunshine.db.nlb.NlbRouteEntity
import hibernate.v2.sunshine.db.nlb.NlbRouteStopEntity
import hibernate.v2.sunshine.db.nlb.NlbStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataService.getNlbData() }

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
                            .map(NlbRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(NlbRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { NlbRouteStop ->
                                NlbRouteStopEntity.fromApiModel(NlbRouteStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { NlbStop ->
                                NlbStopEntity.fromApiModel(NlbStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<NlbRouteEntity>) {
        nlbDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<NlbRouteStopEntity>) {
        nlbDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<NlbStopEntity>) {
        nlbDao.addStopList(entityList)
    }
}
