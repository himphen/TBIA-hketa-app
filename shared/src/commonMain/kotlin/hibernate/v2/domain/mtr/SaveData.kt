package hibernate.v2.domain.mtr

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.mtr.MtrDao
import hibernate.v2.utils.CommonLogger
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke() {
        val result = ApiSafeCall { DataRepository.getMtrData() }

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
                            .apply { sortWith(MtrRoute::compareTo) }
                            .let {
                                mtrDao.addRouteList(it)
                            }
                    }
                    logLifecycle("MtrInteractor saveRouteList done")
                },
                async {
                    data.routeStop?.let { list ->
                        mtrDao.addRouteStopList(list)
                    }
                    logLifecycle("MtrInteractor saveRouteStopList done")
                },
                async {
                    data.stop?.let { list ->
                        mtrDao.addStopList(list)
                    }
                    logLifecycle("MtrInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }
}
