package hibernate.v2.domain.nlb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.nlb.NlbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.utils.logLifecycle
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
                async {
                    data.route?.let { list ->
                        list.toMutableList()
                            .apply { sortWith(NlbRoute::compareTo) }
                            .let {
                                nlbDao.addRouteList(it)
                            }
                    }
                    logLifecycle("NlbInteractor saveRouteList done")
                },
                async {
                    data.routeStop?.let { list ->
                        nlbDao.addRouteStopList(list)
                    }
                    logLifecycle("NlbInteractor saveRouteStopList done")
                },
                async {
                    data.stop?.let { list ->
                        nlbDao.addStopList(list)
                    }
                    logLifecycle("NlbInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }
}
