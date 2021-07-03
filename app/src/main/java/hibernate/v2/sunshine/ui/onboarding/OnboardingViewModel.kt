package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnboardingViewModel(
    private val kmbRepository: KmbRepository,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData<Boolean>()
    val fetchTransportDataCompleted = MutableLiveData<Boolean>()

    fun checkDbTransportData() {
        viewModelScope.launch {
            val deferredList = listOf(
                async {
                    kmbRepository.isDataExisted()
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
            try {
                listOf(
                    async {
                        kmbRepository.saveStopListApi()
                        Logger.d("lifecycle saveStopListApi done")
                    },
                    async {
                        kmbRepository.saveRouteListApi()
                        Logger.d("lifecycle saveRouteListApi done")
                    },
                    async {
                        kmbRepository.saveRouteStopListApi()
                        Logger.d("lifecycle saveRouteStopListApi done")
                    }
                ).awaitAll()

                Logger.d("lifecycle downloadTransportData done")
                fetchTransportDataCompleted.postValue(true)
            } catch (e: Exception) {
                Logger.e(e, "lifecycle downloadTransportData error")
                fetchTransportDataCompleted.postValue(false)
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
