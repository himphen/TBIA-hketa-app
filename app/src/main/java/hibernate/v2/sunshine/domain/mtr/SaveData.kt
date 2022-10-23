package hibernate.v2.sunshine.domain.mtr

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.service.DataService
import hibernate.v2.sunshine.db.mtr.MtrDao
import hibernate.v2.sunshine.db.mtr.MtrRouteEntity
import hibernate.v2.sunshine.db.mtr.MtrRouteStopEntity
import hibernate.v2.sunshine.db.mtr.MtrStopEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataService.getMtrData() }

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
                            .map(MtrRouteEntity.Companion::fromApiModel)
                            .toMutableList()
                            .apply { sortWith(MtrRouteEntity::compareTo) }

                        saveRouteList(temp)
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        saveRouteStopList(
                            list.map { routeStop ->
                                MtrRouteStopEntity.fromApiModel(routeStop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        saveStopList(
                            list.map { stop ->
                                MtrStopEntity.fromApiModel(stop)
                            }
                        )
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }

    private suspend fun saveRouteList(entityList: List<MtrRouteEntity>) {
        mtrDao.addRouteList(entityList)
    }

    private suspend fun saveRouteStopList(entityList: List<MtrRouteStopEntity>) {
        mtrDao.addRouteStopList(entityList)
    }

    private suspend fun saveStopList(entityList: List<MtrStopEntity>) {
        mtrDao.addStopList(entityList)
    }
}
