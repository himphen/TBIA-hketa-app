package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.repository.KmbRepository
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
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData<Boolean>()
    val fetchTransportDataCompleted = MutableLiveData<FetchTransportDataType>()
    val fetchTransportDataFailed = MutableLiveData<FetchTransportDataType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            ncRepository.initDatabase()

            val deferredList = listOf(
                async { kmbRepository.isDataExisted() },
                async { ncRepository.isDataExisted() },
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
                    Logger.d("lifecycle downloadKmbTransportData kmbRepository.saveStopListApi done")
                },
                launch {
                    kmbRepository.saveRouteListApi()
                    Logger.d("lifecycle downloadKmbTransportData kmbRepository.saveRouteListApi done")
                },
                launch {
                    kmbRepository.saveRouteStopListApi()
                    Logger.d("lifecycle downloadKmbTransportData kmbRepository.saveRouteStopListApi done")
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
                    Logger.d("lifecycle downloadNCTransportData ncRepository.saveRouteListFromFirebase done")
                },
                launch {
                    ncRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData ncRepository.saveRouteStopListFromFirebase done")
                },
                launch {
                    ncRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData ncRepository.saveStopListFromFirebase done")
                }
            ).joinAll()

        }
    }
}

enum class FetchTransportDataType {
    KMB, NC, ALL
}
