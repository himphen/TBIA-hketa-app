package hibernate.v2.sunshine.ui.eta.add.leanback

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter

class AddEtaCardPresenter(
    context: Context,
    private val listener: ClickListener
) : BaseCardPresenter<AddEtaCardView, Card.RouteStopAddCard>(context) {

    interface ClickListener {
        fun onItemClick(card: Card.RouteStopAddCard)
    }

    override fun onCreateView(): AddEtaCardView {
        val cardView = AddEtaCardView(context)
        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background_focussed)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        cardView.setOnClickListener { listener.onItemClick(it.tag as Card.RouteStopAddCard) }
        return cardView
    }

    override fun onBindViewHolder(card: Card.RouteStopAddCard, cardView: AddEtaCardView) {
        cardView.tag = card
        cardView.updateDataUi(card)
    }

    private fun setImageBackground(cardView: AddEtaCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }
}