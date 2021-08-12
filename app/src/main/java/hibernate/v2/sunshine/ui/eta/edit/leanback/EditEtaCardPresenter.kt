package hibernate.v2.sunshine.ui.eta.edit.leanback

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class EditEtaCardPresenter(
    context: Context,
    private val listener: ClickListener
) : BaseCardPresenter<EditEtaCardView, Card.SettingsEtaCard>(context) {

    interface ClickListener {
        fun onItemClick(card: Card.SettingsEtaCard)
    }

    override fun onCreateView(): EditEtaCardView {
        val cardView = EditEtaCardView(context)
        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        return cardView
    }

    override fun onBindViewHolder(card: Card.SettingsEtaCard, cardView: EditEtaCardView) {
        cardView.tag = card
        cardView.setOnClickListener { listener.onItemClick(it.tag as Card.SettingsEtaCard) }

        when (card) {
            is Card.SettingsEtaItemCard -> cardView.onBindItemView(card)
            else -> cardView.onBindAddView()
        }
    }

    private fun setImageBackground(cardView: EditEtaCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}