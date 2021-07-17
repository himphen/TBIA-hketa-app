package hibernate.v2.sunshine.ui.eta.mobile

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil.formatString

abstract class BaseEtaCardViewHolder<T : ViewBinding>(open val viewBinding: ViewBinding) :
    RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun onBind(card: Card.EtaCard)

    val context: Context
        get() {
            return viewBinding.root.context
        }

    fun getEtaTimeText(etaList: List<TransportEta>): String {
        var string = ""

        etaList.forEach {
            string += it.eta.formatString(DateFormat.HH_MM.value) + "    "
        }

        return string.trim()
    }
}