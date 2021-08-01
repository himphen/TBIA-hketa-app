package hibernate.v2.sunshine.ui.eta.mobile.view

import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card

class EtaViewHolderClassic(viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(false)

            routeCompanyColor.setBackgroundResource(color)
            routeNumberTv.text = card.route.routeNo
            stopNameTv.text = card.stop.nameTc
            routeDirectionTv.text = card.route.getDestDirectionText(context)
            etaMinuteTv.text =
                card.etaList.getOrNull(0)?.getEtaMinuteText(context) ?: "-"
            etaTimeTv.text = getEtaTimeText(card.etaList)
        }
    }
}