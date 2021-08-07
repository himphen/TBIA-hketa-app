package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData<Boolean>()
    val fetchTransportDataCompleted = MutableLiveData<FetchTransportDataType>()
    val fetchTransportDataFailed = MutableLiveData<FetchTransportDataType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredList = listOf(
                async { kmbRepository.isDataExisted() },
                async { ncRepository.isDataExisted() },
                async { gmbRepository.isDataExisted() },
                async { mtrRepository.isDataExisted() },
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
                downloadKmbTransportData()
                fetchTransportDataCompleted.postValue(FetchTransportDataType.KMB)
            } catch (e: Exception) {
                Logger.e(e, "=== lifecycle downloadKmbTransportData error ===")
                fetchTransportDataFailed.postValue(FetchTransportDataType.KMB)
                return@launch
            }

            try {
                downloadNCTransportData()
                fetchTransportDataCompleted.postValue(FetchTransportDataType.NC)
            } catch (e: Exception) {
                Logger.e(e, "=== lifecycle downloadNCTransportData error ===")
                fetchTransportDataFailed.postValue(FetchTransportDataType.NC)
                return@launch
            }

            try {
                downloadGmbTransportData()
                fetchTransportDataCompleted.postValue(FetchTransportDataType.GMB)
            } catch (e: Exception) {
                Logger.e(e, "=== lifecycle downloadGmbTransportData error ===")
                fetchTransportDataFailed.postValue(FetchTransportDataType.GMB)
                return@launch
            }

            try {
                downloadMTRTransportData()
                fetchTransportDataCompleted.postValue(FetchTransportDataType.MTR)
            } catch (e: Exception) {
                Logger.e(e, "=== lifecycle downloadMTRTransportData error ===")
                fetchTransportDataFailed.postValue(FetchTransportDataType.MTR)
                return@launch
            }

            fetchTransportDataCompleted.postValue(FetchTransportDataType.ALL)
        }
    }

    private suspend fun downloadKmbTransportData() {
        Logger.d("=== lifecycle downloadKmbTransportData start ===")
        kmbRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    kmbRepository.saveStopListApi()
                    Logger.d("lifecycle downloadKmbTransportData saveStopListApi done")
                },
                launch {
                    kmbRepository.saveRouteListApi()
                    Logger.d("lifecycle downloadKmbTransportData saveRouteListApi done")
                },
                launch {
                    kmbRepository.saveRouteStopListApi()
                    Logger.d("lifecycle downloadKmbTransportData saveRouteStopListApi done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadNCTransportData() {
        Logger.d("=== lifecycle downloadNCTransportData start ===")
        ncRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    ncRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveRouteListFromFirebase done")
                },
                launch {
                    ncRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    ncRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadGmbTransportData() {
        Logger.d("=== lifecycle downloadGmbTransportData start ===")
        gmbRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    gmbRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveRouteListFromFirebase done")
                },
                launch {
                    gmbRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    gmbRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadMTRTransportData() {
        Logger.d("=== lifecycle downloadNCTransportData start ===")
        mtrRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    mtrRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveRouteListFromFirebase done")
                },
                launch {
                    mtrRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    mtrRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    suspend fun resetTransportData() {
        kmbRepository.initDatabase()
        ncRepository.initDatabase()
        gmbRepository.initDatabase()
        mtrRepository.initDatabase()
    }
}

enum class FetchTransportDataType {
    KMB, NC, GMB, MTR, ALL
}
