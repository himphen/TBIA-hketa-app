package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.himphen.logger.Logger
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.Company
import hibernate.v2.api.model.nc.NCRoute
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCRouteWithRouteStop
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingViewModel(
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData<Boolean>()
    val fetchTransportDataCompleted = MutableLiveData<FetchTransportDataCompleted>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            ncRepository.initDatabase()

            val database =
                Firebase.database("https://android-tv-c733a-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val routeRef = database.reference.child("nwfb_citybus_route")
            val routeStopRef = database.reference.child("nwfb_citybus_route_stop")
            val stopRef = database.reference.child("nwfb_citybus_stop")

            routeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<List<NCRoute>>()?.let {
                        Logger.d(it)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Logger.e(error.toException(), "Failed to read value.")
                }
            })

//            val deferredList = listOf(
//                async {
//                    kmbRepository.isDataExisted()
//                },
//                async {
//                    ncRepository.isDataExisted()
//                },
//            )
//            val result = deferredList.awaitAll().toMutableList()
//
//            if (result.contains(false)) {
//                fetchTransportDataRequired.postValue(true)
//            } else {
//                fetchTransportDataRequired.postValue(false)
//            }
        }
    }

    fun downloadTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("=== lifecycle downloadTransportData start ===")
            try {
                kmbRepository.initDatabase()
                listOf(
                    async {
                        kmbRepository.saveStopListApi()
                        Logger.d("lifecycle downloadTransportData kmbRepository.saveStopListApi done")
                    },
                    async {
                        kmbRepository.saveRouteListApi()
                        Logger.d("lifecycle downloadTransportData kmbRepository.saveRouteListApi done")
                    },
                    async {
                        kmbRepository.saveRouteStopListApi()
                        Logger.d("lifecycle downloadTransportData kmbRepository.saveRouteStopListApi done")
                    }
                ).awaitAll()
                fetchTransportDataCompleted.postValue(FetchTransportDataCompleted.KMB)
                Logger.d("lifecycle downloadTransportData kmbRepository done")

                // Get NWFB/CTB data
                ncRepository.initDatabase()
                val companyList = listOf(Company.NWFB, Company.CTB)
                companyList.forEach { company ->
                    val routeStopDeferredList = ncRepository
                        .getRouteListApi(company)
                        .routeList.map { route ->
                            async {
                                val list = mutableListOf<NCRouteWithRouteStop>()
                                ncRepository.getRouteWithRouteStop(route, Bound.OUTBOUND)?.let {
                                    list.add(it)
                                }
                                ncRepository.getRouteWithRouteStop(route, Bound.INBOUND)?.let {
                                    list.add(it)
                                }
                                Logger.d("lifecycle downloadTransportData ncRepository.getRouteListApi ${route.routeId} $list done")
                                return@async list
                            }
                        }.awaitAll()

                    val routeList = mutableListOf<NCRouteEntity>()
                    val routeStopList = mutableListOf<NCRouteStopEntity>()
                    routeStopDeferredList.flatten().forEach {
                        routeList.add(it.routeEntity)
                        routeStopList.addAll(it.entityList)
                    }

                    ncRepository.saveRouteList(routeList)
                    ncRepository.saveRouteStopList(routeStopList)
                    Logger.d("lifecycle downloadTransportData ncRepository.saveRouteStopList $company done")

                    // Wait for stop id list to fetch stop detail from api
                    val stopDeferredList =
                        routeStopList
                            .map { ncRouteStopEntity ->
                                ncRouteStopEntity.stopId
                            }
                            .distinct()
                            .map { stopId ->
                                async {
                                    ncRepository.getStopApi(stopId).stop?.let { ncStop ->
                                        NCStopEntity.fromApiModel(ncStop)
                                    }
                                }
                            }.awaitAll()
                    val stopList = stopDeferredList.toMutableList().filterNotNull()
                    ncRepository.saveStopList(stopList)
                }
                fetchTransportDataCompleted.postValue(FetchTransportDataCompleted.NC)

                Logger.d("=== lifecycle downloadTransportData done ===")
                fetchTransportDataCompleted.postValue(FetchTransportDataCompleted.ALL)
            } catch (e: Exception) {
                Logger.e(e, "=== lifecycle downloadTransportData error ===")
                fetchTransportDataCompleted.postValue(FetchTransportDataCompleted.ALL)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}

enum class FetchTransportDataCompleted {
    KMB, NC, ALL
}
