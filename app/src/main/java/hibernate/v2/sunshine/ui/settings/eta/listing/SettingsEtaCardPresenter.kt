package hibernate.v2.sunshine.ui.settings.eta.listing

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class SettingsEtaCardPresenter(
    context: Context,
    private val listener: ClickListener
) : BaseCardPresenter<SettingsEtaCardView, Card.SettingsEtaCard>(context) {

    interface ClickListener {
        fun onItemClick(card: Card.SettingsEtaCard)
    }

    override fun onCreateView(): SettingsEtaCardView {
        val cardView = SettingsEtaCardView(context)
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

    override fun onBindViewHolder(card: Card.SettingsEtaCard, cardView: SettingsEtaCardView) {
        cardView.tag = card
        cardView.setOnClickListener { listener.onItemClick(it.tag as Card.SettingsEtaCard) }

        when (card.type) {
            Card.SettingsEtaCard.Type.DATA -> cardView.updateDataUi(card)
            else -> cardView.updateInsertUi()
        }
    }

    private fun setImageBackground(cardView: SettingsEtaCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}