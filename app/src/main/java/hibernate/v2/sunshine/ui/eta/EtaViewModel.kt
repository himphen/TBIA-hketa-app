package hibernate.v2.sunshine.ui.eta

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.RouteEtaStop
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EtaViewModel(
    application: Application,
    private val repo: DataRepository
) : BaseViewModel() {

    private val etaRepository = EtaRepository.getInstance(
        application.applicationContext
    )

    val etaEntityList = MutableLiveData<List<EtaEntity>>()
    val routeEtaStopList = MutableLiveData<List<RouteEtaStop>>()
    val routeHashMap = MutableLiveData<HashMap<String, Route>>()
    val stopHashMap = MutableLiveData<HashMap<String, Stop>>()
    val routeAndStopListReady = MutableLiveData<Boolean>()

    suspend fun getEtaListFromDb() {
        val savedEtaEntityList = etaRepository.getEtaList()

        if (savedEtaEntityList.isNotEmpty()) {
            etaEntityList.postValue(savedEtaEntityList)
        } else {
            val defaultEtaEntityList = mutableListOf<EtaEntity>()
            defaultEtaEntityList.add(
                EtaEntity(
                    stopId = "9D208FE6B2CFD450",
                    routeId = "290",
                    bound = Bound.OUTBOUND,
                    serviceType = "1",
                    seq = "2"
                )
            )
            defaultEtaEntityList.add(
                EtaEntity(
                    stopId = "9D208FE6B2CFD450",
                    routeId = "290X",
                    bound = Bound.OUTBOUND,
                    serviceType = "1",
                    seq = "12"
                )
            )
            defaultEtaEntityList.add(
                EtaEntity(
                    stopId = "403881982F9E7209",
                    routeId = "296A",
                    bound = Bound.OUTBOUND,
                    serviceType = "1",
                    seq = "1"
                )
            )
            defaultEtaEntityList.add(
                EtaEntity(
                    stopId = "5527FF8CC85CF139",
                    routeId = "296C",
                    bound = Bound.OUTBOUND,
                    serviceType = "1",
                    seq = "1"
                )
            )
            defaultEtaEntityList.add(
                EtaEntity(
                    stopId = "21E3E95EAEB2048C",
                    routeId = "296D",
                    bound = Bound.OUTBOUND,
                    serviceType = "1",
                    seq = "1"
                )
            )
            etaEntityList.postValue(defaultEtaEntityList)
        }
    }

    fun getRouteAndStopList() {
        viewModelScope.launch {
            routeAndStopListReady.postValue(false)
            try {
                val stopResult = hashMapOf<String, Stop>()
                val routeResult = hashMapOf<String, Route>()

                val list = etaEntityList.value ?: run {
                    throw Exception("etaEntityList is null")
                }

                val deferredStopResult = list.map { entity ->
                    entity.stopId
                }.distinct().map { stopId ->
                    async {
                        val apiStopResponse = repo.getStop(stopId)
                        apiStopResponse.stop?.let { apiStop ->
                            stopResult[stopId] = apiStop
                        }
                        Logger.d("lifecycle getStop done: $stopId")
                    }
                }

                val deferredRouteResult = list.associate { entity ->
                    val request = entity.toRouteRequest()
                    request.hashId() to request
                }.map { (_, route) ->
                    async {
                        val apiStopResponse = repo.getRoute(
                            route.routeId,
                            route.bound,
                            route.serviceType
                        )
                        apiStopResponse.route?.let { apiRoute ->
                            routeResult[route.hashId()] = apiRoute
                        }
                        Logger.d("lifecycle getRoute done: " + route.hashId())
                    }
                }

                val deferred = deferredStopResult + deferredRouteResult
                deferred.awaitAll()

                Logger.d("lifecycle getRouteAndStopList done")
                routeHashMap.postValue(routeResult)
                stopHashMap.postValue(stopResult)
                routeAndStopListReady.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getRouteAndStopList error")
                routeAndStopListReady.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    fun getRouteEtaStopList() {
        viewModelScope.launch {
            Logger.d("lifecycle getEtaList")
            try {
                val routeEtaStopHashMapResult = hashMapOf<String, RouteEtaStop>()
                val list = etaEntityList.value!!
                list.map { entity ->
                    async {
                        val stop = stopHashMap.value?.get(entity.stopId)
                        val route =
                            routeHashMap.value?.get(entity.routeHashId())

                        if (stop == null || route == null) return@async

                        val apiEtaResponse =
                            repo.getStopEta(entity.stopId, entity.routeId)
                        apiEtaResponse.data?.let { etaList ->
                            routeEtaStopHashMapResult.put(
                                entity.routeHashId(),
                                RouteEtaStop(
                                    route = route,
                                    stop = stop,
                                    etaList = etaList.filter { eta ->
                                        // Filter not same bound in bus terminal stops
                                        eta.dir == entity.bound && eta.seq == entity.seq
                                    }
                                )
                            )
                        }
                        Logger.d("lifecycle getStopEta done: " + entity.stopId + "-" + entity.routeId)
                    }
                }.awaitAll()

                val routeEtaStopResult = list.mapNotNull { entity ->
                    routeEtaStopHashMapResult[entity.routeHashId()]
                }

                Logger.d("lifecycle getEtaList done")
                routeEtaStopList.postValue(routeEtaStopResult)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getEtaList error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
