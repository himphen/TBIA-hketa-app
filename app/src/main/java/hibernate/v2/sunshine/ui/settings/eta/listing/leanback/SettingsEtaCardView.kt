package hibernate.v2.sunshine.ui.settings.eta.listing.leanback

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.LbCardSettingsEtaBinding
import hibernate.v2.sunshine.model.Card

class SettingsEtaCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = LbCardSettingsEtaBinding.inflate(LayoutInflater.from(context), this, true)

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
        val color = when (card.route.company) {
            Company.KMB -> R.color.brand_color_kmb
            Company.NWFB -> R.color.brand_color_nwfb
            Company.CTB -> R.color.brand_color_ctb
            Company.GMB -> R.color.brand_color_gmb
            Company.MTR -> R.color.brand_color_gmb
            Company.UNKNOWN -> R.color.eta_card_bg_selected
        }

        viewBinding.routeCompanyColor.setBackgroundResource(color)
        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text = card.route.getDestDirectionText(context)
        viewBinding.routeDirectionTv.visibility = VISIBLE
    }

    fun onBindAddView() {
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