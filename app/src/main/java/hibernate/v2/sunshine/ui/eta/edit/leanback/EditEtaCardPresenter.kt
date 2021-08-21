package hibernate.v2.sunshine.ui.eta.edit.leanback

import android.content.Context
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class EditEtaCardPresenter(
    context: Context,
    private val onItemClick: (Card.SettingsEtaCard) -> Unit
) : BaseCardPresenter<EditEtaCardView, Card.SettingsEtaCard>(context) {

    override fun onCreateView(): EditEtaCardView {
        return EditEtaCardView(context)
    }

    override fun onBindViewHolder(card: Card.SettingsEtaCard, cardView: EditEtaCardView) {
        cardView.tag = card
        cardView.setOnClickListener { onItemClick(it.tag as Card.SettingsEtaCard) }

        when (card) {
            is Card.SettingsEtaItemCard -> cardView.onBindItemView(card)
            else -> cardView.onBindAddView()
        }
    }
}