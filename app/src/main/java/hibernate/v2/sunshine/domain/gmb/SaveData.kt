package hibernate.v2.sunshine.domain.gmb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.gmb.GmbDao
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
                        list.toMutableList()
                            .apply { sortWith(GmbRoute::compareTo) }
                            .let {
                                gmbDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        gmbDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("GmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        gmbDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("GmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
