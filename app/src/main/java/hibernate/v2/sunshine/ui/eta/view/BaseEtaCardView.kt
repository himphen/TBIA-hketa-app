package hibernate.v2.sunshine.ui.eta.view

import android.content.Context
import android.util.AttributeSet
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Date

abstract class BaseEtaCardView<T : ViewBinding>(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseCardView(
    context,
    attrs,
    defStyleAttr
) {
    abstract var viewBinding: T

    abstract fun onBind(card: Card.EtaCard)

    fun getEtaTimeText(etaList: List<TransportEta>): String {
        var string = ""

        etaList.forEach {
            string += it.eta.formatString(DateFormat.HH_MM.value) + "    "
        }

        return string.trim()
    }
}