package hibernate.v2.sunshine.ui.settings.eta.listing.mobile

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemSettingsEtaListingBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute

class SettingsEtaViewHolder(
    val viewBinding: ItemSettingsEtaListingBinding,
    private val listener: SettingsEtaListingAdapter.ItemListener,
) : RecyclerView.ViewHolder(viewBinding.root) {

    val context: Context
        get() {
            return viewBinding.root.context
        }

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
        viewBinding.apply {
            val route = card.route
            val color = route.getColor(context, false)

            when (route) {
                is MTRTransportRoute -> {
                    routeNumberTv.visibility = View.GONE
                    routeCompanyColor.visibility = View.GONE
                    routeMTRNumberTv.apply {
                        text = "M"
                        visibility = View.VISIBLE

                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LRTTransportRoute -> {
                    routeNumberTv.visibility = View.GONE
                    routeCompanyColor.visibility = View.GONE
                    routeMTRNumberTv.apply {
                        text = "L"
                        visibility = View.VISIBLE

                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                else -> {
                    routeNumberTv.apply {
                        text = card.route.getCardRouteText()
                        visibility = View.VISIBLE
                    }
                    routeCompanyColor.visibility = View.VISIBLE
                    routeMTRNumberTv.visibility = View.GONE
                    routeCompanyColor.setBackgroundColor(color)
                }
            }

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            root.tag = card
            root.setOnClickListener { listener.onItemClick(it.tag as Card.SettingsEtaItemCard) }
        }
    }
}