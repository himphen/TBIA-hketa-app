package hibernate.v2.sunshine.ui.settings.eta.add

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardAddEtaBinding
import hibernate.v2.sunshine.model.Card

class AddEtaCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.BaseCardStyle
) {
    var viewBinding = CardAddEtaBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateDataUi(card: Card.RouteStopCard) {
        card.stop!!
        viewBinding.stopNameTv.text = card.stop.nameTc
    }

    init {
        isFocusable = true
    }
}