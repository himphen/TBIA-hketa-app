package hibernate.v2.model

import hibernate.v2.core.FlowAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow

abstract class ViewModel {

    val viewModelScope = MainScope()

    /**
     * Override this to do any cleanup immediately before the internal [CoroutineScope][kotlinx.coroutines.CoroutineScope]
     * is cancelled in [clear]
     */
    protected open fun onCleared() {
    }

    /**
     * Cancels the internal [CoroutineScope][kotlinx.coroutines.CoroutineScope]. After this is called, the ViewModel should
     * no longer be used.
     */
    fun clear() {
        onCleared()
        viewModelScope.cancel()
    }
}

abstract class CallbackViewModel {
    protected abstract val viewModel: ViewModel

    /**
     * Create a [FlowAdapter] from this [Flow] to make it easier to interact with from Swift.
     */
    fun <T : Any> Flow<T>.asCallbacks() =
        FlowAdapter(viewModel.viewModelScope, this)

    @Suppress("Unused") // Called from Swift
    fun clear() = viewModel.clear()
}