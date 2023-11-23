package hibernate.v2.tbia.ui.bookmark.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.model.Card
import hibernate.v2.model.transport.route.LrtTransportRoute
import hibernate.v2.model.transport.route.MtrTransportRoute
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.ContentEtaClassicRouteNumberBinding
import hibernate.v2.tbia.databinding.ContentEtaCompactRouteNumberBinding
import hibernate.v2.tbia.databinding.ContentEtaStandardRouteNumberBinding
import hibernate.v2.tbia.util.dpToPx
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.visible

interface EtaRouteView {

    fun applyRouteNumberContainer(
        card: Card.EtaCard,
        routeNumberContainer: ContentEtaClassicRouteNumberBinding
    ) {
        val route = card.route
        val color = route.getColor().colorInt()

        routeNumberContainer.apply {
            when (route) {
                is MtrTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberIv.apply {
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LrtTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeMTRNumberLl.gone()

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
                    routeMTRNumberLl.gone()
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
        val route = card.route
        val color = route.getColor().colorInt()

        routeNumberContainer.apply {
            when (route) {
                is MtrTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberIv.apply {
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LrtTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeMTRNumberLl.gone()

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
                    routeMTRNumberLl.gone()
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
        val route = card.route
        val color = route.getColor().colorInt()

        routeNumberContainer.apply {
            when (route) {
                is MtrTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberIv.apply {
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LrtTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeMTRNumberLl.gone()

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
                    routeMTRNumberLl.gone()
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
        val route = card.route
        val color = route.getColor().colorInt()

        routeNumberContainer.apply {
            when (route) {
                is MtrTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeLRTNumberLl.gone()

                    routeMTRNumberLl.visible()
                    routeMTRNumberIv.apply {
                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LrtTransportRoute -> {
                    routeBusNumberLl.gone()
                    routeCompanyColor.gone()
                    routeMTRNumberLl.gone()

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
                    routeMTRNumberLl.gone()
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
