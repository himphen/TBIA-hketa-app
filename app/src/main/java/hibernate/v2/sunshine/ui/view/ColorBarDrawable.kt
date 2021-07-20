package hibernate.v2.sunshine.ui.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable

class ColorBarDrawable(private val themeColors: IntArray) : Drawable() {
    override fun draw(canvas: Canvas) {

        // get drawable dimensions
        val bounds: Rect = bounds
        val width: Int = bounds.right - bounds.left
        val height: Int = bounds.bottom - bounds.top

        // draw background gradient
        val backgroundPaint = Paint()
        val barWidth = width.toFloat() / themeColors.size
        val barWidthRemainder = width % themeColors.size
        for (i in themeColors.indices) {
            backgroundPaint.color = themeColors[i]
            canvas.drawRect(
                i * barWidth,
                0f,
                (i + 1) * barWidth,
                height.toFloat(),
                backgroundPaint
            )
        }

        // draw remainder, if exists
        if (barWidthRemainder > 0) {
            canvas.drawRect(
                themeColors.size * barWidth,
                0f,
                themeColors.size * barWidth + barWidthRemainder,
                height.toFloat(),
                backgroundPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(cf: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}