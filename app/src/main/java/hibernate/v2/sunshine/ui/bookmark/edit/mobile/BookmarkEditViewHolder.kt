package hibernate.v2.sunshine.ui.bookmark.edit.mobile

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.model.Card
import hibernate.v2.sunshine.databinding.ItemBookmarkEditBinding
import hibernate.v2.sunshine.ui.bookmark.view.EtaRouteView

class BookmarkEditViewHolder(
    val viewBinding: ItemBookmarkEditBinding,
    private val onItemClick: (Card.SettingsEtaItemCard) -> Unit,
) : RecyclerView.ViewHolder(viewBinding.root), EtaRouteView {

    val context: Context
        get() {
            return viewBinding.root.context
        }

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
        viewBinding.apply {
            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getLocalisedName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            root.tag = card
            root.setOnClickListener { onItemClick(it.tag as Card.SettingsEtaItemCard) }
        }
    }
}
