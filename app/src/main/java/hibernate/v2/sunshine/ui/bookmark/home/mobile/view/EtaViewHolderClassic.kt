package hibernate.v2.sunshine.ui.bookmark.home.mobile.view

import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaCardClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.bookmark.view.EtaRouteView
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EtaViewHolderClassic(viewBinding: ItemEtaCardClassicBinding) :
    BaseEtaViewHolder<ItemEtaCardClassicBinding>(viewBinding),
    EtaRouteView {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            (etaMinuteLl.etaTimeRv.adapter as EtaTimeAdapter).apply {
                setData(card.etaList)
            }

            card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                etaMinuteLl.etaMinuteTv.text = it.second

                if (it.first) {
                    etaMinuteLl.etaMinuteUnitTv.visible()
                } else {
                    etaMinuteLl.etaMinuteUnitTv.gone()
                }
            } ?: run {
                etaMinuteLl.etaMinuteTv.text =
                    context.getString(R.string.demo_card_eta_minute_na)
                etaMinuteLl.etaMinuteUnitTv.gone()
            }
        }
    }
}
