package hibernate.v2.domain.mtr

import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.mtr.MtrRoute
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.database.mtr.MtrDao
import kotlinx.coroutines.Dispatchers
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
                async(Dispatchers.IO) {
                    data.route?.let { list ->
                        list.toMutableList()
                            .apply { sortWith(MtrRoute::compareTo) }
                            .let {
                                mtrDao.addRouteList(it)
                            }
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveRouteList done")
                },
                async(Dispatchers.IO) {
                    data.routeStop?.let { list ->
                        mtrDao.addRouteStopList(list)
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveRouteStopList done")
                },
                async(Dispatchers.IO) {
                    data.stop?.let { list ->
                        mtrDao.addStopList(list)
                    }
                    Logger.t("lifecycle").d("MtrInteractor saveStopList done")
                }
            ).awaitAll()
        }
    }
}
