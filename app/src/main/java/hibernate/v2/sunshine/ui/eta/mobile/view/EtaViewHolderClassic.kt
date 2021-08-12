package hibernate.v2.sunshine.ui.eta.mobile.view

import android.graphics.drawable.GradientDrawable
import android.view.View
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EtaViewHolderClassic(viewBinding: ItemEtaClassicBinding) :
    BaseEtaViewHolder<ItemEtaClassicBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(context, false)

            if (route is MTRTransportRoute) {
                routeNumberTv.gone()
                routeCompanyColor.gone()
                routeMTRNumberTv.apply {
                    text = card.platform
                    visible()

                    (background as? GradientDrawable)?.apply {
                        mutate()
                        setColor(color)
                    }
                }
            } else {
                routeNumberTv.apply {
                    text = card.route.getCardRouteText()
                    visible()
                }
                routeCompanyColor.visible()
                routeMTRNumberTv.gone()
                routeCompanyColor.setBackgroundColor(color)
            }

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)
            card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                etaMinuteTv.text = it.second

                if (it.first) {
                    etaMinuteUnitTv.visible()
                    etaTimeTv.text = getEtaTimeText(card.etaList)
                    etaTimeTv.visible()
                } else {
                    etaMinuteTv.text = "-"
                    etaMinuteUnitTv.gone()
                    etaTimeTv.gone()
                }
            } ?: run {
                etaMinuteTv.text = "-"
                etaMinuteUnitTv.gone()
                etaTimeTv.gone()
            }
        }
    }
}