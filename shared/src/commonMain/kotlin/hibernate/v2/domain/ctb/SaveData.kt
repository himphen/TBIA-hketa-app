package hibernate.v2.domain.ctb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SaveData(
    private val dataRepository: DataRepository,
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { dataRepository.getCtbData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        coroutineScope {
            listOf(
                async {
                    logLifecycle("NCRepository saveRouteList start")
                    data.route?.let { list ->
                        ctbDao.addRouteList(list)
                    }
                    logLifecycle("NCRepository saveRouteList done")
                },
                async {
                    logLifecycle("NCRepository saveRouteStopList start")
                    data.routeStop?.let { list ->
                        ctbDao.addRouteStopList(list)
                    }
                    logLifecycle("NCRepository saveRouteStopList done")
                },
                async {
                    logLifecycle("NCRepository saveStopList start")
                    data.stop?.let { list ->
                        ctbDao.addStopList(list)
                    }
                    logLifecycle("NCRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
