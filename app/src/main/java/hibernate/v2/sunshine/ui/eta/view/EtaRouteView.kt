package hibernate.v2.sunshine.ui.eta.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaClassicRouteNumberBinding
import hibernate.v2.sunshine.databinding.ContentEtaCompactRouteNumberBinding
import hibernate.v2.sunshine.databinding.ContentEtaStandardRouteNumberBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

interface EtaRouteView {

    fun applyRouteNumberContainer(
        card: Card.EtaCard,
        routeNumberContainer: ContentEtaClassicRouteNumberBinding
    ) {
        val context = routeNumberContainer.root.context
        val route = card.route
        val color = route.getColor(context, false)

        routeNumberContainer.apply {
            when (route) {
                is MTRTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberTv.apply {
                        text = card.platform
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

                    routeLRTNumberLl.visible()
                    routeLRTNumberTv.apply {
                        text = route.routeNo
                        (background as? LayerDrawable)?.apply {
                            mutate()
                            (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                setStroke(dpToPx(4), color)
                            }
                        }
                    }
                }
                else -> {
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.gone()

                    routeBusNumberLl.visible()
                    routeBusNumberTv.apply {
                        text = route.getCardRouteText()
                        visible()
                    }
                    routeCompanyColor.visible()
                    routeCompanyColor.setBackgroundColor(color)
                }
            }
        }
    }

    fun applyRouteNumberContainer(
        card: Card.EtaCard,
        routeNumberContainer: ContentEtaCompactRouteNumberBinding
    ) {
        val context = routeNumberContainer.root.context
        val route = card.route
        val color = route.getColor(context, false)

        routeNumberContainer.apply {
            when (route) {
                is MTRTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberTv.apply {
                        text = card.platform
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LRTTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeMTRNumberTv.gone()

                    routeLRTNumberLl.visible()
                    routeLRTNumberTv.apply {
                        text = route.routeNo
                        (background as? LayerDrawable)?.apply {
                            mutate()
                            (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                setStroke(dpToPx(4), color)
                            }
                        }
                    }
                }
                else -> {
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.gone()

                    routeBusNumberLl.visible()
                    routeBusNumberTv.apply {
                        text = route.getCardRouteText()
                        visible()
                    }
                }
            }
        }
    }

    fun applyRouteNumberContainer(
        card: Card.EtaCard,
        routeNumberContainer: ContentEtaStandardRouteNumberBinding
    ) {
        val context = routeNumberContainer.root.context
        val route = card.route
        val color = route.getColor(context, false)

        routeNumberContainer.apply {
            when (route) {
                is MTRTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberTv.apply {
                        text = card.platform
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LRTTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeMTRNumberTv.gone()

                    routeLRTNumberLl.visible()
                    routeLRTNumberTv.apply {
                        text = route.routeNo
                        (background as? LayerDrawable)?.apply {
                            mutate()
                            (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                setStroke(dpToPx(4), color)
                            }
                        }
                    }
                }
                else -> {
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.gone()

                    routeBusNumberLl.visible()
                    routeBusNumberTv.apply {
                        text = route.getCardRouteText()
                        visible()
                    }
                }
            }
        }
    }

    fun applyRouteNumberContainer(
        card: Card.SettingsEtaItemCard,
        routeNumberContainer: ContentEtaClassicRouteNumberBinding
    ) {
        val context = routeNumberContainer.root.context
        val route = card.route
        val color = route.getColor(context, false)

        routeNumberContainer.apply {
            when (route) {
                is MTRTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberTv.apply {
                        text = "M"
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

                    routeLRTNumberLl.visible()
                    routeLRTNumberTv.apply {
                        text = route.routeNo
                        (background as? LayerDrawable)?.apply {
                            mutate()
                            (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                setStroke(dpToPx(4), color)
                            }
                        }
                    }
                }
                else -> {
                    routeMTRNumberTv.gone()
                    routeLRTNumberTv.gone()

                    routeBusNumberLl.visible()
                    routeBusNumberTv.apply {
                        text = route.getCardRouteText()
                        visible()
                    }
                    routeCompanyColor.visible()
                    routeCompanyColor.setBackgroundColor(color)
                }
            }
        }
    }
}
