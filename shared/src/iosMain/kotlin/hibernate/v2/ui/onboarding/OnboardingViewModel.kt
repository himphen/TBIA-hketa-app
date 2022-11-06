package hibernate.v2.ui.onboarding

import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.api.repository.CoreRepository
import hibernate.v2.core.IOSMainScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OnboardingViewModel(
    private val onDataState: (Checksum) -> Unit
) : KoinComponent {

    private val coreRepository: CoreRepository by inject()

    private val scope = IOSMainScope(Dispatchers.Main)

    fun checkDbTransportData() {
        scope.launch {
            val serverChecksum = coreRepository.getChecksum()

            onDataState(serverChecksum)
        }
    }
}