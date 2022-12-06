package hibernate.v2.tbia.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import hibernate.v2.tbia.R

internal class MyGuidanceStylingRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private val mTitleKeylinePercent: Float = getKeyLinePercent(context)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val mTitleView = rootView.findViewById<View>(R.id.guidance_title)
        val mBreadcrumbView = rootView.findViewById<View>(R.id.guidance_breadcrumb)
        val mDescriptionView = rootView.findViewById<View>(
            R.id.guidance_description
        )
        val mIconView = rootView.findViewById<ImageView>(R.id.guidance_icon)
        val mTitleKeylinePixels = (measuredHeight * mTitleKeylinePercent / 100).toInt()
        if (mTitleView != null && mTitleView.parent === this) {
            val titleViewBaseline = mTitleView.baseline
            val mBreadcrumbViewHeight = mBreadcrumbView?.measuredHeight ?: 0
            val guidanceTextContainerTop = (
                mTitleKeylinePixels -
                    titleViewBaseline - mBreadcrumbViewHeight - mTitleView.paddingTop
                )
            val offset = guidanceTextContainerTop - mBreadcrumbView.top
            if (mBreadcrumbView != null && mBreadcrumbView.parent === this) {
                mBreadcrumbView.offsetTopAndBottom(offset)
            }
            mTitleView.offsetTopAndBottom(offset)
            if (mDescriptionView != null && mDescriptionView.parent === this) {
                mDescriptionView.offsetTopAndBottom(offset)
            }
        }
        if (mIconView != null && mIconView.parent === this) {
            val drawable = mIconView.drawable
            if (drawable != null) {
                mIconView.offsetTopAndBottom(
                    mTitleKeylinePixels - mIconView.measuredHeight / 2
                )
            }
        }
    }

    companion object {
        fun getKeyLinePercent(context: Context): Float {
            val ta = context.theme.obtainStyledAttributes(
                R.styleable.LeanbackGuidedStepTheme
            )
            val percent = ta.getFloat(
                androidx.leanback.R.styleable.LeanbackGuidedStepTheme_guidedStepKeyline,
                40f
            )
            ta.recycle()
            return percent
        }
    }
}
