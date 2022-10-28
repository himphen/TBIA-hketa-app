package hibernate.v2.sunshine.domain.kmb

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.kmb.KmbRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.LocalDatabaseKmm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SaveData(
    private val localDatabaseKmm: LocalDatabaseKmm
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
                                localDatabaseKmm.saveRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        localDatabaseKmm.saveRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        localDatabaseKmm.saveStopList(list)
                    }
                    Logger.t("lifecycle").d("KmbRepository saveStopList done")
                }
            ).awaitAll()
        }
    }
}
