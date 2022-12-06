package hibernate.v2.domain.lrt

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SaveData(
    private val dataRepository: DataRepository,
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { dataRepository.getLrtData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        coroutineScope {
            listOf(
                async {
                    logLifecycle("LrtRepository saveRouteList start")
                    data.route?.let { list ->
                        lrtDao.addRouteList(list)
                    }
                    logLifecycle("LrtRepository saveRouteList done")
                },
                async {
                    logLifecycle("LrtRepository saveRouteList start")
                    data.routeStop?.let { list ->
                        lrtDao.addRouteStopList(list)
                    }
                    logLifecycle("LrtRepository saveRouteStopList done")
                },
                async {
                    logLifecycle("LrtRepository saveRouteList start")
                    data.stop?.let { list ->
                        lrtDao.addStopList(list)
                    }
                    logLifecycle("LrtRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
