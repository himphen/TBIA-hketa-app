package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.kmb.Bound
import hibernate.v2.api.model.nc.Company
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
            val deferredList = listOf(
                async {
                    kmbRepository.isDataExisted()
                },
                async {
                    ncRepository.isDataExisted()
                },
            )
            val result = deferredList.awaitAll().toMutableList()

            if (result.contains(false)) {
                fetchTransportDataRequired.postValue(true)
            } else {
                fetchTransportDataRequired.postValue(false)
            }
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

                // Get NWFB/CTB data
                ncRepository.initDatabase()
                val companyList = listOf(Company.NWFB, Company.CTB)
                companyList.forEach { company ->
                    val stopIdDeferredList = ncRepository
                        .getRouteListApi(company)
                        .routeList.map { route ->
                            async {
                                val stopIdList = mutableListOf<String>()
                                stopIdList.addAll(ncRepository.saveRoute(route, Bound.OUTBOUND))
                                stopIdList.addAll(ncRepository.saveRoute(route, Bound.INBOUND))
                                Logger.d("lifecycle downloadTransportData ncRepository.getRouteListApi ${route.routeId} $stopIdList done")
                                return@async stopIdList
                            }
                        }.awaitAll()
                    // Wait for stop id list to fetch stop detail from api
                    val stopDeferredList = stopIdDeferredList
                        .flatten().distinct().map { stopId ->
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
