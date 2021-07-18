package hibernate.v2.sunshine.ui.settings.leanback

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import coil.load
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class SettingsIconPresenter(
    context: Context,
    private val listener: ClickListener
) : BaseCardPresenter<SettingsCardView, Card.SettingsCard>(
    ContextThemeWrapper(context, R.style.Theme_Fragment_SettingsEta)
) {
    interface ClickListener {
        fun onItemClick(card: Card.SettingsCard)
    }

    override fun onCreateView(): SettingsCardView {
        val cardView = SettingsCardView(context)
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
        cardView: SettingsCardView
    ) {
        cardView.tag = card
        cardView.viewBinding.settingsTitle.text = card.title
        cardView.viewBinding.settingsIcon.load(card.icon)
    }

    private fun setImageBackground(cardView: SettingsCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}
