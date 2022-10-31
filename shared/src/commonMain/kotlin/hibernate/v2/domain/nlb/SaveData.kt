package hibernate.v2.domain.nlb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.nlb.NlbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.nlb.NlbDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getNlbData() }

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
                            .apply { sortWith(NlbRoute::compareTo) }
                            .let {
                                nlbDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        nlbDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        nlbDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("NlbInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }
}
