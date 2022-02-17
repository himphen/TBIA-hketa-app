package hibernate.v2.sunshine.ui.eta.edit.mobile

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemEditEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.view.EtaRouteView

class EditEtaViewHolder(
    val viewBinding: ItemEditEtaBinding,
    private val onItemClick: (Card.SettingsEtaItemCard) -> Unit,
) : RecyclerView.ViewHolder(viewBinding.root), EtaRouteView {

    val context: Context
        get() {
            return viewBinding.root.context
        }

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
        viewBinding.apply {
            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            root.tag = card
            root.setOnClickListener { onItemClick(it.tag as Card.SettingsEtaItemCard) }
        }
    }
}
