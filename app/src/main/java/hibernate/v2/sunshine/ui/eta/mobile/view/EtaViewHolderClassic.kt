package hibernate.v2.sunshine.ui.eta.mobile.view

import android.graphics.drawable.GradientDrawable
import android.view.View
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute

class EtaViewHolderClassic(viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(context, false)

            if (route is MTRTransportRoute) {
                routeNumberTv.visibility = View.GONE
                routeCompanyColor.visibility = View.GONE
                routeMTRNumberTv.apply {
                    text = card.platform
                    visibility = View.VISIBLE

                    (background as? GradientDrawable)?.apply {
                        mutate()
                        setColor(color)
                    }
                }
            } else if (route is LRTTransportRoute) {
                routeNumberTv.visibility = View.GONE
                routeCompanyColor.visibility = View.GONE
                routeMTRNumberTv.apply {
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

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)
            card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                etaMinuteTv.text = it.second

                if (it.first) {
                    etaMinuteUnitTv.visibility = View.VISIBLE
                    etaTimeTv.text = getEtaTimeText(card.etaList)
                    etaTimeTv.visibility = View.VISIBLE
                } else {
                    etaMinuteTv.text = "-"
                    etaMinuteUnitTv.visibility = View.GONE
                    etaTimeTv.visibility = View.GONE
                }
            } ?: run {
                etaMinuteTv.text = "-"
                etaMinuteUnitTv.visibility = View.GONE
                etaTimeTv.visibility = View.GONE
            }
        }
    }
}