package hibernate.v2.sunshine.ui.eta.leanback

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.ui.eta.EtaTimeAdapter
import hibernate.v2.sunshine.ui.eta.leanback.view.BaseEtaCardView
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewClassic
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewCompact
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardViewStandard

class EtaCardPresenter(
    context: Context,
    private val fragmentWidth: Int,
    private val type: EtaCardViewType,
) : BaseCardPresenter<BaseEtaCardView<out ViewBinding>, Card.EtaCard>(
    ContextThemeWrapper(context, R.style.AppTheme_Leanback_Eta)
) {

    override fun onCreateView(): BaseEtaCardView<out ViewBinding> {
        val cardView = when (type) {
            EtaCardViewType.Standard -> EtaCardViewStandard(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            )
            EtaCardViewType.Compact -> EtaCardViewCompact(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            )
            EtaCardViewType.Classic -> EtaCardViewClassic(
                ContextThemeWrapper(
                    context,
                    R.style.FullCardStyle
                )
            ).apply {
                viewBinding.etaTimeRv.apply {
                    layoutManager = FlexboxLayoutManager(context).apply {
                        flexWrap = FlexWrap.WRAP
                        alignItems = AlignItems.CENTER
                        flexDirection = FlexDirection.ROW
                        justifyContent = JustifyContent.FLEX_START
                    }
                    adapter = EtaTimeAdapter()
                }
            }
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
}