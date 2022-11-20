package hibernate.v2.domain.gmb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.gmb.GmbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class SaveData(
    private val dataRepository: DataRepository,
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { dataRepository.getGmbData() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        coroutineScope {
            listOf(
                async {
                    logLifecycle("GmbRepository saveRouteList start")
                    data.route?.let { list ->
                        list.toMutableList()
                            .apply { sortWith(GmbRoute::compareTo) }
                            .let {
                                gmbDao.addRouteList(it)
                            }
                    }
                    logLifecycle("GmbRepository saveRouteList done")
                },
                async {
                    logLifecycle("GmbRepository saveRouteStopList start")
                    data.routeStop?.let { list ->
                        gmbDao.addRouteStopList(list)
                    }
                    logLifecycle("GmbRepository saveRouteStopList done")
                },
                async {
                    logLifecycle("GmbRepository saveStopList start")
                    data.stop?.let { list ->
                        gmbDao.addStopList(list)
                    }
                    logLifecycle("GmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
