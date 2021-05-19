package hibernate.v2.sunshine.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

abstract class BaseViewModel : ViewModel() {
    val sError = ConflatedBroadcastChannel<Throwable>()
    val sIsLoading = ConflatedBroadcastChannel<Boolean>()
}
