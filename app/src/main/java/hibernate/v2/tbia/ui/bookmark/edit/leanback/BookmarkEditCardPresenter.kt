package hibernate.v2.tbia.ui.bookmark.edit.leanback

import android.content.Context
import hibernate.v2.model.Card
import hibernate.v2.tbia.ui.base.BaseCardPresenter

class BookmarkEditCardPresenter(
    context: Context,
    private val onItemClick: (Card.SettingsEtaCard) -> Unit
) : BaseCardPresenter<BookmarkEditCardView, Card.SettingsEtaCard>(context) {

    override fun onCreateView(): BookmarkEditCardView {
        return BookmarkEditCardView(context)
    }

    override fun onBindViewHolder(card: Card.SettingsEtaCard, cardView: BookmarkEditCardView) {
        cardView.tag = card
        cardView.setOnClickListener { onItemClick(it.tag as Card.SettingsEtaCard) }

        when (card) {
            is Card.SettingsEtaItemCard -> cardView.onBindItemView(card)
            else -> cardView.onBindAddView()
        }
    }
}
