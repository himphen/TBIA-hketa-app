package hibernate.v2.sunshine.ui.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<T : ViewBinding>(val viewBinding: T) :
    RecyclerView.ViewHolder(viewBinding.root) {

    val context: Context
        get() {
            return viewBinding.root.context
        }
}