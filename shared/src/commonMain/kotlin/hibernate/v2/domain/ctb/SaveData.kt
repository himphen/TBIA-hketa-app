package hibernate.v2.domain.ctb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.ctb.CtbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.ctb.CtbDao
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
                        list.toMutableList()
                            .apply { sortWith(CtbRoute::compareTo) }
                            .let {
                                ctbDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        ctbDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("NCRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        ctbDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("NCRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
