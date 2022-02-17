package hibernate.v2.sunshine.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class TouchDisabledRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    override fun onInterceptTouchEvent(e: MotionEvent?) = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent) = false
}

fun RecyclerView.setEtaTimeFlexManager() {
    layoutManager = FlexboxLayoutManager(context).apply {
        flexWrap = FlexWrap.WRAP
        alignItems = AlignItems.FLEX_END
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.FLEX_END
    }
}

fun RecyclerView.setStopRouteBadgeFlexManager() {
    layoutManager = FlexboxLayoutManager(context).apply {
        flexWrap = FlexWrap.WRAP
        alignItems = AlignItems.CENTER
        flexDirection = FlexDirection.ROW
        justifyContent = JustifyContent.FLEX_START
    }
}
