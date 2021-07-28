package hibernate.v2.sunshine.util

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.widget.addTextChangedListener
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
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

fun View.updateBackgroundColor(@ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        background.colorFilter =
            BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        background.setColorFilter(
            color,
            PorterDuff.Mode.SRC_ATOP
        )
    }
}

fun View.slideToBottomAnimate(show: Boolean) {
    val transition: Transition = Slide(Gravity.BOTTOM)
    transition.duration = 600
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    visibility = if (show) View.VISIBLE else View.GONE
}