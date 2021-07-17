package hibernate.v2.sunshine.ui.settings.eta.listing

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardSettingsEtaBinding
import hibernate.v2.sunshine.model.Card

class SettingsEtaCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = CardSettingsEtaBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateDataUi(card: Card.SettingsEtaItemCard) {
        val color = when (card.route.company) {
            Company.KMB -> R.color.brand_color_kmb
            Company.NWFB -> R.color.brand_color_nwfb
            Company.CTB -> R.color.brand_color_ctb
            Company.GMB -> R.color.brand_color_gmb
            Company.UNKNOWN -> R.color.eta_card_bg_selected
        }

        viewBinding.routeCompanyColor.setBackgroundResource(color)
        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text =
            context.getString(R.string.text_eta_destination, card.route.destTc)
        viewBinding.routeDirectionTv.visibility = VISIBLE

//        card.stop!!
//        card.route!!
//        viewBinding.stopNameTv.text = card.stop.nameTc
//        viewBinding.routeNumberTv.text = card.route.routeNo
//
//        if (card.route.isSpecialRoute()) {
//            viewBinding.routeDirectionTv.text = context.getString(
//                R.string.text_settings_eta_destination_with_sp,
//                card.route.destTc,
//                card.route.serviceType
//            )
//        } else {
//            viewBinding.routeDirectionTv.text = context.getString(
//                R.string.text_settings_eta_destination,
//                card.route.destTc
//            )
//        }
    }

    fun updateInsertUi() {
        viewBinding.routeCompanyColor.setBackgroundResource(
            R.color.eta_card_bg_selected
        )

        viewBinding.stopNameTv.text = "按此加入"
        viewBinding.routeNumberTv.text = "+"
        viewBinding.routeDirectionTv.visibility = GONE
    }

    init {
        isFocusable = true
    }
}