package hibernate.v2.sunshine.ui.eta.mobile.view

import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class EtaViewHolderClassic(override val viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        val color = when (card.route.company) {
            Company.KMB -> R.color.brand_color_kmb
            Company.NWFB -> R.color.brand_color_nwfb
            Company.CTB -> R.color.brand_color_ctb
            Company.GMB -> R.color.brand_color_gmb
            Company.UNKNOWN -> R.color.eta_card_bg_selected
        }

        viewBinding.content.routeCompanyColor.setBackgroundResource(color)
        viewBinding.content.routeNumberTv.text = card.route.routeNo
        viewBinding.content.stopNameTv.text = card.stop.nameTc
        viewBinding.content.routeDirectionTv.text = card.route.getDestDirectionText(context)
        viewBinding.content.etaMinuteTv.text =
            getEtaMinuteText(card.etaList.getOrNull(0))
        viewBinding.content.etaTimeTv.text = getEtaTimeText(card.etaList)
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