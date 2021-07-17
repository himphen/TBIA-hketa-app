package hibernate.v2.sunshine.ui.eta.leanback.view

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class EtaCardPresenter(
    context: Context,
    private val fragmentWidth: Int,
    private val type: CardViewType
) : BaseCardPresenter<BaseEtaCardView<out ViewBinding>, Card.EtaCard>(
    ContextThemeWrapper(context, R.style.Theme_Fragment_Eta)
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

        companion object {
            fun from(type: Int?) = values().find { it.value == type } ?: Standard
        }
    }
}