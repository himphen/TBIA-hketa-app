package hibernate.v2.sunshine.ui.onboarding

import com.himphen.logger.Logger
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

class OnboardingViewModel(
    private val kmbRepository: KmbRepository,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableSharedFlow<Boolean>()
    val fetchTransportDataCompleted = MutableSharedFlow<Boolean>()

    suspend fun checkDbTransportData(scope: CoroutineScope) {
        val result = arrayListOf<Boolean>()
        val deferredList = listOf(
            scope.async { result.add(kmbRepository.isDataExisted()) },
        )
        deferredList.awaitAll()

        if (result.contains(false)) {
            fetchTransportDataRequired.emit(true)
        } else {
            fetchTransportDataRequired.emit(false)
        }
    }

    suspend fun downloadTransportData(scope: CoroutineScope) {
        try {
            listOf(
                scope.async {
                    kmbRepository.saveStopListApi()
                    Logger.d("lifecycle saveStopListApi done")
                },
                scope.async {
                    kmbRepository.saveRouteListApi()
                    Logger.d("lifecycle saveRouteListApi done")
                },
                scope.async {
                    kmbRepository.saveRouteStopListApi()
                    Logger.d("lifecycle saveRouteStopListApi done")
                }
            ).awaitAll()

            Logger.d("lifecycle downloadTransportData done")
            fetchTransportDataCompleted.emit(true)
        } catch (e: Exception) {
            Logger.e(e, "lifecycle downloadTransportData error")
            fetchTransportDataCompleted.emit(false)
            withContext(Dispatchers.Main) {
            }
        }
    }
}
