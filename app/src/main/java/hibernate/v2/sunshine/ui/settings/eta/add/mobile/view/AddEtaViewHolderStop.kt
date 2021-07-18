package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import android.view.View
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaStopBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter

class AddEtaViewHolderStop(
    override val viewBinding: ItemAddEtaStopBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaStopBinding>(viewBinding) {

    fun onBind(card: Card.RouteStopAddCard, isFirst: Boolean, isLast: Boolean) {
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.stopSeqTv.text = String.format("%02d", card.stop.seq)

        when (card.route.company) {
            Company.KMB -> {
                viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_kmb)
                viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_kmb)
                viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_kmb)
            }
            Company.NWFB -> {
                viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_nwfb)
                viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_nwfb)
                viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_nwfb)
            }
            Company.CTB -> {
                viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_ctb)
                viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_ctb)
                viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_ctb)
            }
            Company.GMB -> {
                viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_gmb)
                viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_gmb)
                viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_gmb)
            }
            Company.UNKNOWN -> {
            }
        }

        viewBinding.stopLineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
        viewBinding.stopLineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

        viewBinding.root.tag = card
        viewBinding.root.setOnClickListener { listener.onStopSelected(card) }
    }
}