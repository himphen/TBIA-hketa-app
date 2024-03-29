package hibernate.v2.tbia.util

import com.himphen.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration

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

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

// suspend fun DatabaseReference.getSnapshotValue(): DataSnapshot {
//    return withContext(Dispatchers.IO) {
//        suspendCoroutine { continuation ->
//            addListenerForSingleValueEvent(
//                FValueEventListener(
//                    onDataChange = { continuation.resume(it) },
//                    onError = { continuation.resumeWithException(it.toException()) }
//                )
//            )
//        }
//    }
// }

// class FValueEventListener(
//    val onDataChange: (DataSnapshot) -> Unit,
//    val onError: (DatabaseError) -> Unit
// ) :
//    ValueEventListener {
//    override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
//    override fun onCancelled(error: DatabaseError) = onError.invoke(error)
// }
