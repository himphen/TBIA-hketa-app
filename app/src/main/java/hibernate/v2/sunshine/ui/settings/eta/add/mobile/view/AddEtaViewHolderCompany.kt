package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import android.view.View
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaCompanyBinding
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter

class AddEtaViewHolderCompany(
    override val viewBinding: ItemAddEtaCompanyBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaCompanyBinding>(viewBinding) {

    fun onBind(etaType: AddEtaViewModel.EtaType) {
        when (etaType) {
            AddEtaViewModel.EtaType.KMB -> {
                viewBinding.routeCompanyColorTop.setBackgroundResource(R.color.brand_color_kmb)
                viewBinding.routeCompanyColorBottom.visibility = View.GONE
                viewBinding.routeCompanyTv.text =
                    context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
            }
            AddEtaViewModel.EtaType.NWFB_CTB -> {
                viewBinding.routeCompanyColorTop.setBackgroundResource(R.color.brand_color_nwfb)
                viewBinding.routeCompanyColorBottom.setBackgroundResource(R.color.brand_color_ctb)
                viewBinding.routeCompanyColorBottom.visibility = View.VISIBLE
                viewBinding.routeCompanyTv.text =
                    context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
            }
            AddEtaViewModel.EtaType.GMB -> {
                viewBinding.routeCompanyColorTop.setBackgroundResource(R.color.brand_color_gmb)
                viewBinding.routeCompanyColorBottom.visibility = View.GONE
                viewBinding.routeCompanyTv.text =
                    context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
            }
        }

        viewBinding.root.tag = etaType
        viewBinding.root.setOnClickListener { listener.onEtaTypeSelected(etaType) }
    }
}