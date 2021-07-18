package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaCompanyBinding
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.RouteStop

class AddEtaViewHolderCompany(
    override val viewBinding: ItemAddEtaCompanyBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaCompanyBinding>(viewBinding) {

    fun onBind(etaType: AddEtaViewModel.EtaType) {
        viewBinding.companyTv.text = when (etaType) {
            AddEtaViewModel.EtaType.KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
            AddEtaViewModel.EtaType.NWFB_CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
            AddEtaViewModel.EtaType.GMB -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
        }

        viewBinding.root.tag = etaType
        viewBinding.root.setOnClickListener { listener.onEtaTypeSelected (etaType) }
    }
}