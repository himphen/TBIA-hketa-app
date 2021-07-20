package hibernate.v2.sunshine.ui.settings.eta.listing.mobile

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemSettingsEtaListingBinding
import hibernate.v2.sunshine.model.Card

class SettingsEtaViewHolder(
    val viewBinding: ItemSettingsEtaListingBinding,
    private val listener: SettingsEtaListingAdapter.ItemListener
) : RecyclerView.ViewHolder(viewBinding.root) {

    val context: Context
        get() {
            return viewBinding.root.context
        }

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
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
        viewBinding.routeDirectionTv.text = card.route.getDestDirectionText(context)
        viewBinding.routeDirectionTv.visibility = View.VISIBLE

        viewBinding.root.tag = card
        viewBinding.root.setOnClickListener { listener.onItemClick(it.tag as Card.SettingsEtaItemCard) }
    }
}