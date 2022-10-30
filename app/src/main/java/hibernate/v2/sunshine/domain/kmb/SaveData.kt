package hibernate.v2.sunshine.domain.kmb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.kmb.KmbDao
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
                        list.toMutableList()
                            .apply { sortWith(KmbRoute::compareTo) }
                            .let {
                                kmbDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        kmbDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        kmbDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
