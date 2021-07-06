package hibernate.v2.sunshine.util

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.himphen.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

suspend fun DatabaseReference.getSnapshotValue(): DataSnapshot {
    return withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            addListenerForSingleValueEvent(FValueEventListener(
                onDataChange = { continuation.resume(it) },
                onError = { continuation.resumeWithException(it.toException()) }
            ))
        }
    }
}

class FValueEventListener(
    val onDataChange: (DataSnapshot) -> Unit,
    val onError: (DatabaseError) -> Unit
) :
    ValueEventListener {
    override fun onDataChange(data: DataSnapshot) = onDataChange.invoke(data)
    override fun onCancelled(error: DatabaseError) = onError.invoke(error)
}