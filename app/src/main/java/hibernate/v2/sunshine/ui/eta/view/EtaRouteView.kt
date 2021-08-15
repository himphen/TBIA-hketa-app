package hibernate.v2.sunshine.ui.eta.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaRouteNumberBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

interface EtaRouteView {

    fun applyRouteNumberContainer(
        card: Card.EtaCard,
        routeNumberContainer: ContentEtaRouteNumberBinding,
        showRouteColor: Boolean
    ) {
        val context = routeNumberContainer.root.context
        val route = card.route
        val color = route.getColor(context, false)

        routeNumberContainer.apply {
            when (route) {
                is MTRTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeLRTNumberTv.gone()
                    routeMTRNumberTv.apply {
                        text = card.platform
                        visible()

                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LRTTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.apply {
                        text = route.routeNo
                        visible()

                        (background as? LayerDrawable)?.apply {
                            mutate()
                            (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                setStroke(dpToPx(4), color)
                            }
                        }
                    }
                }
                else -> {
                    routeBusNumberLl.visible()
                    routeBusNumberTv.apply {
                        text = route.getCardRouteText()
                        visible()
                    }
                    if (showRouteColor) {
                        routeCompanyColor.visible()
                    } else {
                        routeCompanyColor.gone()
                    }
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.gone()
                    routeCompanyColor.setBackgroundColor(color)
                }
            }
        }
    }
}