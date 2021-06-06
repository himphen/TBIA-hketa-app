package hibernate.v2.sunshine.ui.settings

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import com.bumptech.glide.Glide
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.card.AbstractCardPresenter

/**
 * This Presenter will display cards which consists of a single icon which will be highlighted by a
 * surrounding circle when the card is focused. AndroidTV uses these cards for entering settings
 * menu.
 */
class SettingsIconPresenter(
    context: Context,
    private val listener: ClickListener
) : AbstractCardPresenter<ImageCardView, Card.SettingsCard>(
    ContextThemeWrapper(context, R.style.IconCardTheme)
) {
    interface ClickListener {
        fun onItemClick(card: Card.SettingsCard)
    }

    override fun onCreateView(): ImageCardView {
        val cardView = ImageCardView(context)
        cardView.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        cardView.setOnClickListener { listener.onItemClick(it.tag as Card.SettingsCard) }
        return cardView
    }

    override fun onBindViewHolder(
        card: Card.SettingsCard,
        cardView: ImageCardView
    ) {
        cardView.tag = card
        cardView.titleText = card.title
        cardView.contentText = card.description
        val resourceId = card.getLocalImageResourceId(context)
        if (resourceId != 0) {
            Glide.with(context)
                .asBitmap()
                .load(resourceId)
                .into(cardView.mainImageView)
        }
    }

    private fun setImageBackground(cardView: ImageCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}
