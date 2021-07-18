package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import hibernate.v2.sunshine.databinding.ItemAddEtaRouteBinding
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter

class AddEtaViewHolderRoute(
    override val viewBinding: ItemAddEtaRouteBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaRouteBinding>(viewBinding) {

    fun onBind(route: RouteForRowAdapter) {
        viewBinding.routeNumberTv.text = route.route.routeNo

        viewBinding.root.tag = route
        viewBinding.root.setOnClickListener { listener.onRouteSelected (route) }
    }
}