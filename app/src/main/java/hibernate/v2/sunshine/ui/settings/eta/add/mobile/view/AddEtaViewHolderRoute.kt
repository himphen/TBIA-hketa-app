package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import android.content.Context
import android.view.View
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaRouteBinding
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter

class AddEtaViewHolderRoute(
    override val viewBinding: ItemAddEtaRouteBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaRouteBinding>(viewBinding) {

    fun onBind(route: RouteForRowAdapter) {
        viewBinding.routeNumberTv.text = route.route.routeNo
        viewBinding.routeDirectionTv.text = context.getString(
            R.string.text_add_eta_destination_mobile,
            route.route.origTc,
            route.route.destTc
        )

        viewBinding.routeSpTv.apply {
            if (route.route.isSpecialRoute()) {
                text = context.getString(R.string.text_add_eta_destination_sp_mobile, route.route.serviceType)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }

        viewBinding.root.tag = route
        viewBinding.root.setOnClickListener { listener.onRouteSelected(route) }
    }

    fun getDirectionText(context: Context, route: TransportRoute): String {
        return context.getString(
            R.string.text_add_eta_destination_mobile,
            route.origTc,
            route.destTc
        )
    }
}