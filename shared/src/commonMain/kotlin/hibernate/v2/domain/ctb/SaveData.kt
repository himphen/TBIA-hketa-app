package hibernate.v2.domain.ctb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.ctb.CtbRoute
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
                    data.route?.let { list ->
                        list.toMutableList()
                            .apply { sortWith(CtbRoute::compareTo) }
                            .let {
                                ctbDao.addRouteList(it)
                            }
                    }
                    logLifecycle("NCRepository saveRouteList done")
                },
                async {
                    data.routeStop?.let { list ->
                        ctbDao.addRouteStopList(list)
                    }
                    logLifecycle("NCRepository saveRouteStopList done")
                },
                async {
                    data.stop?.let { list ->
                        ctbDao.addStopList(list)
                    }
                    logLifecycle("NCRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
