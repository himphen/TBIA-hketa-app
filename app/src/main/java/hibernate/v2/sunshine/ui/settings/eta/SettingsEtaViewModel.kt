package hibernate.v2.sunshine.ui.settings.eta

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.api.response.RouteStopListResponse
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.RouteStopList
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsEtaViewModel(
    application: Application,
    private val repo: DataRepository
) : BaseViewModel() {

    val etaRepository = EtaRepository.getInstance(
        application.applicationContext
    )

    val routeHashMap = MutableLiveData<HashMap<String, Route>>()
    val stopHashMap = MutableLiveData<HashMap<String, Stop>>()
    val routeStopListHashMap = MutableLiveData<HashMap<String, RouteStopList>>()
    val routeAndStopListReady = MutableLiveData<Boolean>()

    suspend fun getData() = withContext(Dispatchers.IO) { etaRepository.getData() }

    fun clearData(item: EtaEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearData(item)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearAllData()
            }
        }
    }

    fun getAllRouteAndStopList() {
        viewModelScope.launch {
            routeAndStopListReady.postValue(false)
            try {
                val stopResult = hashMapOf<String, Stop>()
                val routeResult = hashMapOf<String, Route>()
                val routeStopListResult = hashMapOf<String, RouteStopList>()

                var apiRouteStopListResponse: RouteStopListResponse? = null

                val deferred = listOf(
                    async {
                        val apiStopListResponse = repo.getStopList()
                        apiStopListResponse.stopList.forEach { stop ->
                            stop.stopId.let { stopId ->
                                stopResult[stopId] = stop
                            }
                        }
                        Logger.d("lifecycle getStopList done")
                    },
                    async {
                        val apiRouteListResponse = repo.getRouteList()
                        apiRouteListResponse.routeList.forEach { route ->
                            routeResult[route.routeHashId()] = route
                            routeStopListResult[route.routeHashId()] = RouteStopList(
                                route = route,
                                stopList = mutableListOf()
                            )
                        }
                        Logger.d("lifecycle getRouteList done")
                    },
                    async {
                        apiRouteStopListResponse = repo.getRouteStopList()
                        Logger.d("lifecycle getRouteStopList done")
                    }
                )
                deferred.awaitAll()

                apiRouteStopListResponse?.routeStopList?.forEach { routeStop ->
                    val stop = stopResult[routeStop.stopId]!!
                    stop.seq = routeStop.seq

                    routeStopListResult[routeStop.routeHashId()]?.stopList?.add(stop)
                }
                Logger.d("lifecycle apiRouteStopListResponse done")

                Logger.d("lifecycle getAllRouteAndStopList done")
                routeHashMap.postValue(routeResult)
                stopHashMap.postValue(stopResult)
                routeStopListHashMap.postValue(routeStopListResult)
                routeAndStopListReady.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getAllRouteAndStopList error")
                routeAndStopListReady.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
