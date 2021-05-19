package hibernate.v2.sunshine.ui.eta

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.RouteEtaStop
import hibernate.v2.api.model.Stop
import hibernate.v2.api.request.EtaRequest
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EtaViewModel(private val repo: DataRepository) : BaseViewModel() {
    val routeEtaStopList = MutableLiveData<List<RouteEtaStop>>()
    val routeHashMap = MutableLiveData<HashMap<String, Route>>()
    val stopHashMap = MutableLiveData<HashMap<String, Stop>>()
    val routeAndStopListReady = MutableLiveData<Boolean>()

    fun getRouteAndStopList(etaRequestList: List<EtaRequest>) {
        Logger.d("lifecycle getRouteAndStopList")
        routeAndStopListReady.postValue(false)
        viewModelScope.launch {
            try {
                val stopResult = hashMapOf<String, Stop>()
                val routeResult = hashMapOf<String, Route>()

                coroutineScope {
                    etaRequestList.map { request ->
                        request.stopId
                    }.distinct().forEach { stopId ->
                        launch etaResult@{
                            val apiStopResponse = repo.getStop(stopId)
                            apiStopResponse.stop?.let { apiStop ->
                                stopResult[stopId] = apiStop
                            }
                        }
                    }

                    etaRequestList.map { request ->
                        request.route
                    }.distinct().forEach { route ->
                        launch etaResult@{
                            val apiStopResponse = repo.getRoute(route.routeId, route.bound)
                            apiStopResponse.route?.let { apiRoute ->
                                routeResult[route.routeId + route.bound] = apiRoute
                            }
                        }
                    }
                }

                routeHashMap.postValue(routeResult)
                stopHashMap.postValue(stopResult)
                routeAndStopListReady.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "net error")
                routeAndStopListReady.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    fun getEtaList(etaRequestList: List<EtaRequest>) {
        Logger.d("lifecycle getEtaList")
        viewModelScope.launch {
            try {
                val routeEtaStopResult = arrayListOf<RouteEtaStop>()

                coroutineScope {
                    etaRequestList.forEach { request ->
                        launch EtaAPI@{ // this will allow us to run multiple tasks in parallel
                            val stop = stopHashMap.value?.get(request.stopId)
                            val route =
                                routeHashMap.value?.get(request.route.routeId + request.route.bound)

                            if (stop == null || route == null) return@EtaAPI

                            val apiEtaResponse =
                                repo.getStopEta(request.stopId, request.route.routeId)
                            apiEtaResponse.data?.let { etaList ->
                                routeEtaStopResult.add(
                                    RouteEtaStop(
                                        route = route,
                                        stop = stop,
                                        etaList = etaList
                                    )
                                )
                            }
                        }
                    }
                }  // coroutineScope block will wait here until all child tasks are completed

                routeEtaStopList.postValue(routeEtaStopResult)
            } catch (e: Exception) {
                Logger.e(e, "net error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
