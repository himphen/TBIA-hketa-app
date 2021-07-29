package hibernate.v2.sunshine.ui.eta.mobile.view

import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil.formatString

abstract class BaseEtaViewHolder<T : ViewBinding>(viewBinding: T) :
    BaseViewHolder<T>(viewBinding) {

    abstract fun onBind(card: Card.EtaCard)

    fun getEtaTimeText(etaList: List<TransportEta>): String {
        var string = ""

        etaList.forEach {
            string += it.eta.formatString(DateFormat.HH_MM.value) + "    "
        }

        return string.trim()
    }
}