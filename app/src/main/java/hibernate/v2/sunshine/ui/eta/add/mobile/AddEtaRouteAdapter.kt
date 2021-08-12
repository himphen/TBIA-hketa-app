package hibernate.v2.sunshine.ui.eta.add.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaRouteBinding
import hibernate.v2.sunshine.model.RouteForRowAdapter
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.eta.add.mobile.AddEtaRouteAdapter.AddEtaViewHolderRoute
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class AddEtaRouteAdapter(
    val etaType: EtaType,
    private val onRouteSelected: (RouteForRowAdapter) -> Unit,
) : RecyclerView.Adapter<AddEtaViewHolderRoute>() {

    private var list = mutableListOf<RouteForRowAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AddEtaViewHolderRoute(
            ItemAddEtaRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            if (etaType == EtaType.MTR) {
                viewBinding.routeNumberTv.textSize = 16f

                viewBinding.routeNumberLl.apply {
                    val newLayoutParams =
                        viewBinding.routeNumberLl.layoutParams as ConstraintLayout.LayoutParams
                    newLayoutParams.width = dpToPx(80)
                    requestLayout()
                }
            }
        }

    override fun onBindViewHolder(holder: AddEtaViewHolderRoute, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setRouteData(mutableList: List<RouteForRowAdapter>?) {
        if (mutableList == null) return

        list.clear()
        list.addAll(mutableList)
        notifyDataSetChanged()
    }

    inner class AddEtaViewHolderRoute(
        viewBinding: ItemAddEtaRouteBinding,
    ) : BaseViewHolder<ItemAddEtaRouteBinding>(viewBinding) {

        fun onBind(item: RouteForRowAdapter) {
            viewBinding.apply {
                routeNumberTv.apply {
                    text = if (etaType == EtaType.MTR) {
                        val route = item.route as MTRTransportRoute
                        route.routeInfo.nameTc
                    } else {
                        item.route.routeNo
                    }
                }

                routeCompanyColor.apply {
                    if (etaType == EtaType.MTR || etaType == EtaType.LRT) {
                        setBackgroundColor(item.route.getColor(context))
                    }
                }

                routeOrigTv.text = item.route.origTc
                routeDestTv.text = item.route.destTc

                routeSpTv.apply {
                    if (item.route.isSpecialRoute()) {
                        text = context.getString(
                            R.string.text_add_eta_destination_sp_mobile,
                            item.route.serviceType
                        )
                        visible()
                    } else {
                        gone()
                    }
                }

                root.tag = item
                root.setOnClickListener { onRouteSelected(item) }
            }
        }

        fun getDirectionText(context: Context, route: TransportRoute): String {
            return context.getString(
                R.string.text_add_eta_destination_mobile,
                route.origTc,
                route.destTc
            )
        }
    }
}