package hibernate.v2.sunshine.ui.eta.mobile.view

import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.EtaTimeAdapter
import hibernate.v2.sunshine.ui.eta.view.EtaRouteView
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EtaViewHolderClassic(viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding), EtaRouteView {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            applyRouteNumberContainer(card, routeNumberContainer, true)

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            (etaTimeRv.adapter as EtaTimeAdapter).apply {
                setData(card.etaList)
            }

            card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                etaMinuteTv.text = it.second

                if (it.first) {
                    etaMinuteUnitTv.visible()
                } else {
                    etaMinuteUnitTv.gone()
                }
            } ?: run {
                etaMinuteTv.text = "-"
                etaMinuteUnitTv.gone()
            }
        }
    }
}