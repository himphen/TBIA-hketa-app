package hibernate.v2.sunshine.ui.eta.mobile.view

import android.view.View
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
            card.etaList.getOrNull(0)?.getEtaMinuteText()?.let {
                etaMinuteTv.text = it
                etaMinuteUnitTv.visibility = View.VISIBLE
                etaTimeTv.text = getEtaTimeText(card.etaList)
                etaTimeTv.visibility = View.VISIBLE
            } ?: run {
                etaMinuteTv.text = ""
                etaMinuteUnitTv.visibility = View.GONE
                etaTimeTv.visibility = View.GONE
            }
        }
    }
}