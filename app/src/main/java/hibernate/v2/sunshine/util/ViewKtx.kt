package hibernate.v2.sunshine.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
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

/** Set the View visibility to VISIBLE and eventually animate the View alpha till 100% */
fun View.visible(animate: Boolean = false) {
    if (animate) {
        animate().alpha(1f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = View.VISIBLE
            }
        })
    } else {
        visibility = View.VISIBLE
    }
}

/** Set the View visibility to INVISIBLE and eventually animate view alpha till 0% */
fun View.invisible(animate: Boolean = false) {
    hide(View.INVISIBLE, animate)
}

/** Set the View visibility to GONE and eventually animate view alpha till 0% */
fun View.gone(animate: Boolean = false) {
    hide(View.GONE, animate)
}

/** Convenient method that chooses between View.visible() or View.invisible() methods */
fun View.visibleOrInvisible(show: Boolean, animate: Boolean = false) {
    if (show) visible(animate) else invisible(animate)
}

/** Convenient method that chooses between View.visible() or View.gone() methods */
fun View.visibleOrGone(show: Boolean, animate: Boolean = false) {
    if (show) visible(animate) else gone(animate)
}

private fun View.hide(hidingStrategy: Int, animate: Boolean = false) {
    if (animate) {
        animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = hidingStrategy
            }
        })
    } else {
        visibility = hidingStrategy
    }
}

fun View.toggleSlideDown(show: Boolean, duration: Long = 500) {
    val transition: Transition = Slide(Gravity.BOTTOM)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    visibility = if (show) View.VISIBLE else View.GONE
}

fun View.toggleSlideUp(show: Boolean, duration: Long = 500) {
    val transition: Transition = Slide(Gravity.TOP)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    visibility = if (show) View.VISIBLE else View.GONE
}

fun RecyclerView.smoothSnapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START
) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}
