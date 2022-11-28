package hibernate.v2.tbia.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.VerticalGridPresenter
import androidx.leanback.widget.VerticalGridView
import hibernate.v2.tbia.R

class FullWidthGridPresenter : VerticalGridPresenter {
    constructor() : super()
    constructor(focusZoomFactor: Int) : super(focusZoomFactor)
    constructor(focusZoomFactor: Int, useFocusDimmer: Boolean) : super(
        focusZoomFactor,
        useFocusDimmer
    )

    /**
     * Subclass may override this to inflate a different layout.
     */
    override fun createGridViewHolder(parent: ViewGroup): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(
            R.layout.lb_vertical_grid_full_width, parent, false
        )
        return ViewHolder(root.findViewById<View>(R.id.browse_grid) as VerticalGridView)
    }
}
