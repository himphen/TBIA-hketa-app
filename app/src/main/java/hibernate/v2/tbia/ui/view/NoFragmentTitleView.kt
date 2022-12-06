package hibernate.v2.tbia.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.leanback.widget.TitleViewAdapter

class NoFragmentTitleView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(
    context,
    attrs,
    defStyle
),
    TitleViewAdapter.Provider {

    private val mTitleViewAdapter = object : TitleViewAdapter() {
        override fun getSearchAffordanceView(): View? = null

        override fun setTitle(titleText: CharSequence?) = Unit

        override fun setBadgeDrawable(drawable: Drawable?) = Unit

        override fun setOnSearchClickedListener(listener: OnClickListener) = Unit

        override fun updateComponentsVisibility(flags: Int) {}
    }

    override fun getTitleViewAdapter() = mTitleViewAdapter
}
