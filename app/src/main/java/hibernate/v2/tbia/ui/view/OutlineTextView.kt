package hibernate.v2.tbia.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import hibernate.v2.tbia.R

class OutlineTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {
    private var mOutlineSize = 0
    private var mOutlineColor = 0
    private var mTextColor = 0
    private var mShadowRadius = 0f
    private var mShadowDx = 0f
    private var mShadowDy = 0f
    private var mShadowColor = 0
    private fun setAttributes(attrs: AttributeSet?) {
        // set defaults
        mOutlineSize = DEFAULT_OUTLINE_SIZE
        mOutlineColor = DEFAULT_OUTLINE_COLOR
        // text color
        mTextColor = currentTextColor
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView)
            // outline size
            if (a.hasValue(R.styleable.OutlineTextView_outlineSize)) {
                mOutlineSize =
                    a.getDimension(
                        R.styleable.OutlineTextView_outlineSize,
                        DEFAULT_OUTLINE_SIZE.toFloat()
                    )
                        .toInt()
            }
            // outline color
            if (a.hasValue(R.styleable.OutlineTextView_outlineColor)) {
                mOutlineColor =
                    a.getColor(R.styleable.OutlineTextView_outlineColor, DEFAULT_OUTLINE_COLOR)
            }
            // shadow (the reason we take shadow from attributes is because we use API level 15 and only from 16 we have the get methods for the shadow attributes)
            if (a.hasValue(R.styleable.OutlineTextView_android_shadowRadius) ||
                a.hasValue(R.styleable.OutlineTextView_android_shadowDx) ||
                a.hasValue(R.styleable.OutlineTextView_android_shadowDy) ||
                a.hasValue(R.styleable.OutlineTextView_android_shadowColor)
            ) {
                mShadowRadius = a.getFloat(R.styleable.OutlineTextView_android_shadowRadius, 0f)
                mShadowDx = a.getFloat(R.styleable.OutlineTextView_android_shadowDx, 0f)
                mShadowDy = a.getFloat(R.styleable.OutlineTextView_android_shadowDy, 0f)
                mShadowColor =
                    a.getColor(R.styleable.OutlineTextView_android_shadowColor, Color.TRANSPARENT)
            }
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPaintToOutline()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun setPaintToOutline() {
        val paint: Paint = paint
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = mOutlineSize.toFloat()
        super.setTextColor(mOutlineColor)
        super.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    private fun setPaintToRegular() {
        val paint: Paint = paint
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        super.setTextColor(mTextColor)
        super.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mTextColor = color
    }

    fun setOutlineSize(size: Int) {
        mOutlineSize = size
    }

    fun setOutlineColor(color: Int) {
        mOutlineColor = color
    }

    override fun onDraw(canvas: Canvas) {
        setPaintToOutline()
        super.onDraw(canvas)
        setPaintToRegular()
        super.onDraw(canvas)
    }

    companion object {
        // constants
        private const val DEFAULT_OUTLINE_SIZE = 0
        private const val DEFAULT_OUTLINE_COLOR: Int = Color.TRANSPARENT
    }

    init {
        setAttributes(attrs)
    }
}
