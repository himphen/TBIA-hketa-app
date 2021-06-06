package hibernate.v2.sunshine.ui.settings.eta

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.card.AbstractCardPresenter

/**
 * This Presenter will display a card which consists of a big image followed by a colored footer.
 * Not only the colored footer is unique to this card, but also it's footer (info) will be visible
 * even when its parent row is inactive.
 */
class SettingsEtaCardPresenter(
    context: Context
) : AbstractCardPresenter<BaseCardView, Card.SettingsEtaCard>(
    ContextThemeWrapper(context, R.style.SingleLineCardTheme)
) {
    override fun onCreateView(): BaseCardView {
        val cardView = BaseCardView(context)
        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        cardView.setOnClickListener {
            Toast.makeText(
                context,
                "Clicked on ImageCardView",
                Toast.LENGTH_SHORT
            ).show()
        }
        return cardView
    }

    override fun onBindViewHolder(card: Card.SettingsEtaCard, cardView: BaseCardView) {

    }

    private fun setImageBackground(cardView: BaseCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}