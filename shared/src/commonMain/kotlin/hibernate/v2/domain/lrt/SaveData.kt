package hibernate.v2.domain.lrt

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.lrt.LrtRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.lrt.LrtDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getLrtData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    data.route?.let { list ->
                        list.toMutableList()
                            .apply { sortWith(LrtRoute::compareTo) }
                            .let {
                                lrtDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        lrtDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        lrtDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("LrtInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }
}
