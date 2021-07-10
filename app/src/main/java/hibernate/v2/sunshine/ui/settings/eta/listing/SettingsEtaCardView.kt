package hibernate.v2.sunshine.ui.settings.eta.listing

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardSettingsEtaBinding
import hibernate.v2.sunshine.model.Card

class SettingsEtaCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = CardSettingsEtaBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateDataUi(card: Card.SettingsEtaCard) {
        card.stop!!
        card.route!!
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeNumberTv.text = card.route.routeNo

        if (card.route.serviceType != "1") {
            viewBinding.routeDirectionTv.text = context.getString(
                R.string.text_eta_destination_with_sp,
                card.route.destTc,
                card.route.serviceType
            )
        } else {
            viewBinding.routeDirectionTv.text =
                context.getString(R.string.text_eta_destination, card.route.destTc)
        }
        viewBinding.routeDirectionTv.visibility = VISIBLE
    }

    fun updateInsertUi() {
        viewBinding.stopNameTv.text = "按此加入"
        viewBinding.routeNumberTv.text = "+"
        viewBinding.routeDirectionTv.visibility = GONE
    }

    init {
        isFocusable = true
    }
}