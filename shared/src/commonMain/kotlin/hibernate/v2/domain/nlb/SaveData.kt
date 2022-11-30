package hibernate.v2.domain.nlb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SaveData(
    private val dataRepository: DataRepository,
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { dataRepository.getNlbData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        coroutineScope {
            listOf(
                async {
                    logLifecycle("NlbRepository saveRouteList start")
                    data.route?.let { list ->
                        nlbDao.addRouteList(list)
                    }
                    logLifecycle("NlbRepository saveRouteList done")
                },
                async {
                    logLifecycle("NlbRepository saveRouteStopList start")
                    data.routeStop?.let { list ->
                        nlbDao.addRouteStopList(list)
                    }
                    logLifecycle("NlbRepository saveRouteStopList done")
                },
                async {
                    logLifecycle("NlbRepository saveStopList start")
                    data.stop?.let { list ->
                        nlbDao.addStopList(list)
                    }
                    logLifecycle("NlbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
