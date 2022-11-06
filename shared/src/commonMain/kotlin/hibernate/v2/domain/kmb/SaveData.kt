package hibernate.v2.domain.kmb

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val dataRepository: DataRepository,
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { dataRepository.getKmbData() }

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
                            .apply { sortWith(KmbRoute::compareTo) }
                            .let {
                                kmbDao.addRouteList(it)
                            }
                    }
                    logLifecycle("KmbRepository saveRouteList done")
                },
                async {
                    data.routeStop?.let { list ->
                        kmbDao.addRouteStopList(list)
                    }
                    logLifecycle("KmbRepository saveRouteStopList done")
                },
                async {
                    data.stop?.let { list ->
                        kmbDao.addStopList(list)
                    }
                    logLifecycle("KmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
