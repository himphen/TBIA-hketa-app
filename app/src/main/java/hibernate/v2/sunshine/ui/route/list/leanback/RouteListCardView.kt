package hibernate.v2.sunshine.ui.route.list.leanback

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.LbCardAddEtaStopBinding
import hibernate.v2.sunshine.model.Card

class RouteListCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = LbCardAddEtaStopBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateDataUi(card: Card.RouteStopAddCard) {
        card.stop
        viewBinding.stopNameTv.text = card.stop.getLocalisedName(context)
    }

    init {
        isFocusable = true
    }
}
