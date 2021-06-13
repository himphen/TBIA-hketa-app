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
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
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

    val routeEtaStopList = MutableLiveData<List<Card.RouteEtaStopCard>>()
    private val etaEntityList = MutableLiveData<List<EtaEntity>>()
    private val routeHashMap = MutableLiveData<HashMap<String, Route>>()
    private val stopHashMap = MutableLiveData<HashMap<String, Stop>>()

    suspend fun getEtaListFromDb() {
        val savedEtaEntityList = etaRepository.getEtaList()

        if (savedEtaEntityList.isNotEmpty()) {
            etaEntityList.setValue(savedEtaEntityList)
        } else {
            etaEntityList.setValue(getDefaultEtaEntityList())
        }

        prepareRouteEtaStopList()
    }

    private fun prepareRouteEtaStopList() {
        viewModelScope.launch {
            try {
                val list = etaEntityList.value ?: run {
                    throw Exception("etaEntityList is null")
                }

                val deferredStopResult = getStopList(this, list)
                val deferredRouteResult = getRouteList(this, list)

                val deferred = deferredStopResult + deferredRouteResult
                deferred.awaitAll()

                val routeEtaStopHashMapResult = hashMapOf<String, Card.RouteEtaStopCard>()
                list.map { entity ->
                    async {
                        val stop = stopHashMap.value?.get(entity.stopId)
                        val route =
                            routeHashMap.value?.get(entity.routeHashId())

                        if (stop == null || route == null) return@async

                        routeEtaStopHashMapResult[entity.stopHashId()] = Card.RouteEtaStopCard(
                            entity = entity,
                            route = route,
                            stop = stop,
                            etaList = arrayListOf()
                        )
                        Logger.d("lifecycle getStopEta done: " + entity.stopId + "-" + entity.routeId)
                    }
                }.awaitAll()

                val routeEtaStopResult = list.mapNotNull { entity ->
                    routeEtaStopHashMapResult[entity.stopHashId()]
                }

                routeEtaStopList.postValue(routeEtaStopResult)
                Logger.d("lifecycle initRouteEtaStopList done")
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getRouteAndStopList error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    private fun getStopList(
        scope: CoroutineScope,
        list: List<EtaEntity>
    ): List<Deferred<Unit>> {
        val stopResult = hashMapOf<String, Stop>()
        val deferredStopResult = list.map { entity ->
            entity.stopId
        }.distinct().map { stopId ->
            scope.async {
                val apiStopResponse = repo.getStop(stopId)
                apiStopResponse.stop?.let { apiStop ->
                    stopResult[stopId] = apiStop
                }
                Logger.d("lifecycle getStop done: $stopId")
            }
        }

        stopHashMap.postValue(stopResult)
        return deferredStopResult
    }

    private fun getRouteList(
        scope: CoroutineScope,
        list: List<EtaEntity>
    ): List<Deferred<Unit>> {
        val routeResult = hashMapOf<String, Route>()
        val deferredRouteResult = list.associate { entity ->
            val request = entity.toRouteRequest()
            request.hashId() to request
        }.map { (_, route) ->
            scope.async {
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

        routeHashMap.postValue(routeResult)
        return deferredRouteResult
    }

    fun updateRouteEtaStopList() {
        viewModelScope.launch {
            Logger.d("lifecycle getEtaList")
            try {
                val list = arrayOfNulls<Card.RouteEtaStopCard>(routeEtaStopList.value!!.size)

                routeEtaStopList.value!!.mapIndexed { index, routeEtaStop ->
                    async {
                        val apiEtaResponse = repo.getStopEta(
                            stopId = routeEtaStop.entity.stopId,
                            route = routeEtaStop.entity.routeId
                        )
                        apiEtaResponse.data?.let { etaList ->
                            routeEtaStop.etaList = etaList.filter { eta ->
                                // Filter not same bound in bus terminal stops
                                eta.dir == routeEtaStop.entity.bound && eta.seq == routeEtaStop.entity.seq
                            }
                        }

                        list[index] = routeEtaStop
                        Logger.d("lifecycle getStopEta done: " + routeEtaStop.entity.stopId + "-" + routeEtaStop.entity.routeId)
                    }
                }.awaitAll()

                Logger.d("lifecycle getEtaList done")
                routeEtaStopList.postValue(list.toMutableList().filterNotNull())
            } catch (e: Exception) {
                Logger.e(e, "lifecycle getEtaList error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }

    private fun getDefaultEtaEntityList(): MutableList<EtaEntity> {
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
        return defaultEtaEntityList
    }
}
