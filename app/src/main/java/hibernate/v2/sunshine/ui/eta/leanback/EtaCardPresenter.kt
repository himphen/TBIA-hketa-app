package hibernate.v2.sunshine.ui.eta.leanback

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter
import hibernate.v2.sunshine.ui.eta.leanback.view.BaseEtaCardView
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewClassic
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewCompact
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewStandard

class EtaCardPresenter(
    context: Context,
    private val fragmentWidth: Int,
    private val type: CardViewType
) : BaseCardPresenter<BaseEtaCardView<out ViewBinding>, Card.EtaCard>(
    ContextThemeWrapper(context, R.style.AppTheme_Leanback_Eta)
) {

    override fun onCreateView(): BaseEtaCardView<out ViewBinding> {
        val cardView = when (type) {
            CardViewType.Standard -> EtaCardViewStandard(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            )
            CardViewType.Compact -> EtaCardViewCompact(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            )
            CardViewType.Classic -> EtaCardViewClassic(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            )
        }
        cardView.layoutParams = BaseCardView.LayoutParams(
            fragmentWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.viewBinding.root.layoutParams = BaseCardView.LayoutParams(
            fragmentWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        return cardView
    }

    override fun onBindViewHolder(card: Card.EtaCard, cardView: BaseEtaCardView<out ViewBinding>) {
        cardView.tag = card
        cardView.onBind(card)
    }

    enum class CardViewType(val value: Int) {
        Standard(0), Classic(1), Compact(2);

        fun getTitle(context: Context): String {
            return when (this) {
                Standard -> context.getString(R.string.dialog_eta_layout_standard_btn)
                Classic -> context.getString(R.string.dialog_eta_layout_classic_btn)
                Compact -> context.getString(R.string.dialog_eta_layout_compact_btn)
            }
        }

        companion object {
            fun from(value: Int?) = values().find { it.value == value } ?: Standard
        }
    }
}