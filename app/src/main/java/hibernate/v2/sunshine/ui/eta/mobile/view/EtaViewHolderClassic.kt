package hibernate.v2.sunshine.ui.eta.mobile.view

import android.graphics.drawable.GradientDrawable
import android.view.View
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card

class EtaViewHolderClassic(viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(context, false)

            if (route.company == Company.MTR) {
                routeNumberTv.visibility = View.GONE
                routeCompanyColor.visibility = View.GONE
                routeMTRNumberTv.apply {
                    card.route
                    text = card.platform
                    visibility = View.VISIBLE

                    (background as? GradientDrawable)?.apply {
                        mutate()
                        setColor(color)
                    }
                }
            } else {
                routeNumberTv.apply {
                    text = card.route.getCardRouteText()
                    visibility = View.VISIBLE
                }
                routeCompanyColor.visibility = View.VISIBLE
                routeMTRNumberTv.visibility = View.GONE
                routeCompanyColor.setBackgroundColor(color)
            }

            if (route.company == Company.MTR) {
                stopNameTv.text =
                    context.getString(R.string.text_eta_card_classic_mtr_station, card.stop.nameTc)
            } else {
                stopNameTv.text = card.stop.nameTc
            }

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