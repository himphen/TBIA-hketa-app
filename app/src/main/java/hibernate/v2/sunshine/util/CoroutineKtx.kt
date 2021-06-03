package hibernate.v2.sunshine.util

import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> BaseViewModel.callApiService(
    isSendErrorMsg: Boolean = false,
    callApi: suspend () -> T,
    handlerData: (data: T) -> Unit,
    handlerError: (data: Exception) -> Unit = {}
) {
    viewModelScope.launch {
        sIsLoading.send(true)
        try {
            val data = withContext(Dispatchers.IO) {
                callApi()
            }
            handlerData(data)
        } catch (e: Exception) {
            if (isSendErrorMsg)
                sError.send(e)
            Logger.e(e, "net error")
            withContext(Dispatchers.Main) {
                handlerError(e)
            }
        }
        sIsLoading.send(false)
    }
}

suspend fun <T> retry(numOfRetries: Int, block: suspend () -> T): T {
    var throwable: Throwable? = null
    (1..numOfRetries).forEach { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            throwable = e
            Logger.d("Failed attempt $attempt / $numOfRetries")
        }
    }
    throw throwable!!
}

fun CoroutineScope.launchPeriodicAsync(
    repeatMillis: Long,
    action: () -> Unit
) = this.async {
    if (repeatMillis > 0) {
        while (isActive) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}
