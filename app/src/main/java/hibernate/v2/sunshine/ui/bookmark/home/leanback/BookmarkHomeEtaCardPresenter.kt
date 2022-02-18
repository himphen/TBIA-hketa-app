package hibernate.v2.sunshine.ui.bookmark.home.leanback

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter
import hibernate.v2.sunshine.ui.bookmark.EtaCardViewType
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.bookmark.home.leanback.view.BaseEtaCardView
import hibernate.v2.sunshine.ui.bookmark.home.leanback.view.EtaCardViewClassic
import hibernate.v2.sunshine.ui.bookmark.home.leanback.view.EtaCardViewCompact
import hibernate.v2.sunshine.ui.bookmark.home.leanback.view.EtaCardViewStandard
import hibernate.v2.sunshine.ui.view.setEtaTimeFlexManager

class BookmarkHomeEtaCardPresenter(
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
                viewBinding.etaMinuteLl.etaTimeRv.apply {
                    setEtaTimeFlexManager()
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
