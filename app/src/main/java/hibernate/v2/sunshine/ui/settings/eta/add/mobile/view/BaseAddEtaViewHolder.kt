package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAddEtaViewHolder<T : ViewBinding>(open val viewBinding: ViewBinding) :
    RecyclerView.ViewHolder(viewBinding.root) {

    val context: Context
        get() {
            return viewBinding.root.context
        }
}