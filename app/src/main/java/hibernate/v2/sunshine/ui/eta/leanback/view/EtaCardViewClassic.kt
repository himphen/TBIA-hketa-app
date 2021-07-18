package hibernate.v2.sunshine.ui.eta.leanback.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class EtaCardViewClassic(context: Context) : BaseEtaCardView<CardEtaClassicBinding>(context) {
    override var viewBinding =
        CardEtaClassicBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
        val color = when (card.route.company) {
            Company.KMB -> R.color.brand_color_kmb
            Company.NWFB -> R.color.brand_color_nwfb
            Company.CTB -> R.color.brand_color_ctb
            Company.GMB -> R.color.brand_color_gmb
            Company.UNKNOWN -> R.color.eta_card_bg_selected
        }

        viewBinding.routeCompanyColor.setBackgroundResource(color)
        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text = card.route.getDestDirectionText(context)
        viewBinding.etaMinuteTv.text =
            getEtaMinuteText(card.etaList.getOrNull(0))
        viewBinding.etaTimeTv.text = getEtaTimeText(card.etaList)

        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundResource(R.drawable.eta_card_bg_none_selected)
            } else {
                setBackgroundResource(R.drawable.eta_card_bg_none)
            }
        }
        setBackgroundResource(R.drawable.eta_card_bg_none)
    }

    private fun getEtaMinuteText(eta: TransportEta?): String {
        eta?.eta?.let { etaDate ->
            val minutes = DateUtil.getTimeDiffInMin(
                etaDate,
                Date()
            )
            return (minutes + 1).toString() + " 分鐘"
        } ?: run {
            eta?.rmkTc?.let { rmkTc ->
                if (rmkTc.isNotEmpty()) return rmkTc
            }

            return "-"
        }
    }
}