package hibernate.v2.sunshine.ui.eta.edit.mobile

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemEditEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.LRTTransportRoute
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EditEtaViewHolder(
    val viewBinding: ItemEditEtaBinding,
    private val listener: EditEtaAdapter.ItemListener,
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
                    routeNumberTv.gone()
                    routeCompanyColor.gone()
                    routeMTRNumberTv.apply {
                        text = "M"
                        visible()

                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                is LRTTransportRoute -> {
                    routeNumberTv.gone()
                    routeCompanyColor.gone()
                    routeMTRNumberTv.apply {
                        text = "L"
                        visible()

                        (background as? GradientDrawable)?.apply {
                            mutate()
                            setColor(color)
                        }
                    }
                }
                else -> {
                    routeNumberTv.apply {
                        text = card.route.getCardRouteText()
                        visible()
                    }
                    routeCompanyColor.visible()
                    routeMTRNumberTv.gone()
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