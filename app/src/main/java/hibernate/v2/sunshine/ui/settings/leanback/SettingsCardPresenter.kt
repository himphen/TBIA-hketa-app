package hibernate.v2.sunshine.ui.settings.leanback

import android.content.Context
import android.view.ContextThemeWrapper
import coil.load
import hibernate.v2.model.Card
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class SettingsCardPresenter(
    context: Context,
    private val onItemClick: (Card.SettingsCard) -> Unit
) : BaseCardPresenter<SettingsCardView, Card.SettingsCard>(
    ContextThemeWrapper(context, R.style.AppTheme_Leanback_SettingsEta)
) {
    override fun onCreateView(): SettingsCardView {
        return SettingsCardView(context).apply {
            setOnClickListener { onItemClick(it.tag as Card.SettingsCard) }
        }
    }

    override fun onBindViewHolder(
        card: Card.SettingsCard,
        cardView: SettingsCardView
    ) {
        cardView.tag = card
        cardView.viewBinding.settingsTitle.text = card.title
        cardView.viewBinding.settingsIcon.load(card.icon)
    }
}
