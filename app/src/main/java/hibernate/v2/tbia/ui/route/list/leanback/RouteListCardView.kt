package hibernate.v2.tbia.ui.route.list.leanback

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.model.Card
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.LbCardAddEtaStopBinding

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
