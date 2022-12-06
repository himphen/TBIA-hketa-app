package hibernate.v2.tbia.ui.route.list.leanback

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.model.Card
import hibernate.v2.tbia.R
import hibernate.v2.tbia.ui.base.BaseCardPresenter

class RouteListCardPresenter(
    context: Context,
    private val onItemClick: (Card.RouteStopAddCard) -> Unit,
) : BaseCardPresenter<RouteListCardView, Card.RouteStopAddCard>(context) {

    override fun onCreateView(): RouteListCardView {
        val cardView = RouteListCardView(context)
        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        cardView.setOnClickListener { onItemClick(it.tag as Card.RouteStopAddCard) }
        return cardView
    }

    override fun onBindViewHolder(card: Card.RouteStopAddCard, cardView: RouteListCardView) {
        cardView.tag = card
        cardView.updateDataUi(card)
    }

    private fun setImageBackground(cardView: RouteListCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}
