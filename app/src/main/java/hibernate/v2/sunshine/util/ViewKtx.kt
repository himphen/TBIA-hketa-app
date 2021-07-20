package hibernate.v2.sunshine.util

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun EditText.afterTextChanged(): Flow<String> = callbackFlow {
    val textWatcher = addTextChangedListener(
        afterTextChanged = {
            trySend(it?.toString() ?: "")
        }
    )
    awaitClose { removeTextChangedListener(textWatcher) }
}

fun EditText.onTextChanged(): Flow<String> = callbackFlow {
    val textWatcher = addTextChangedListener(
        onTextChanged = { s, _, _, _ ->
            trySend(s.toString())
        }
    )
    awaitClose { removeTextChangedListener(textWatcher) }
}
