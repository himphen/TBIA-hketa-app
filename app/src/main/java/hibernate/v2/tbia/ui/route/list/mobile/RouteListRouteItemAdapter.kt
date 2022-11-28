package hibernate.v2.tbia.ui.route.list.mobile

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.LrtTransportRoute
import hibernate.v2.model.transport.route.MtrTransportRoute
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.ItemRouteListBinding
import hibernate.v2.tbia.ui.base.BaseViewHolder
import hibernate.v2.tbia.ui.route.list.mobile.RouteListRouteItemAdapter.RouteItemViewHolder
import hibernate.v2.tbia.util.dpToPx
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.visible

class RouteListRouteItemAdapter(
    val etaType: EtaType,
    private val onRouteSelected: (TransportRoute) -> Unit,
) : ListAdapter<TransportRoute, RouteItemViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<TransportRoute>() {
        override fun areItemsTheSame(oldItem: TransportRoute, newItem: TransportRoute): Boolean {
            return oldItem.routeHashId() == newItem.routeHashId()
        }

        override fun areContentsTheSame(oldItem: TransportRoute, newItem: TransportRoute): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RouteItemViewHolder(
            ItemRouteListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            viewBinding.routeNumberContainer.apply {
                when (etaType) {
                    EtaType.MTR -> {
                        val newLayoutParams = root.layoutParams as ConstraintLayout.LayoutParams
                        newLayoutParams.width = dpToPx(100)
                        root.requestLayout()

                        routeCompanyColor.visible()
                        routeBusNumberLl.visible()
                        routeBusNumberTv.textSize = 16f
                        routeMTRNumberLl.gone()
                        routeLRTNumberLl.gone()
                    }
                    EtaType.LRT -> {
                        val newLayoutParams = root.layoutParams as ConstraintLayout.LayoutParams
                        newLayoutParams.width = dpToPx(100)
                        root.requestLayout()
                        routeCompanyColor.gone()
                        routeBusNumberLl.gone()
                        routeMTRNumberLl.gone()
                        routeLRTNumberLl.visible()
                    }
                    else -> {
                        routeCompanyColor.gone()
                        routeBusNumberLl.visible()
                        routeMTRNumberLl.gone()
                        routeLRTNumberLl.gone()
                    }
                }
            }
        }

    override fun onBindViewHolder(holderItemViewHolder: RouteItemViewHolder, position: Int) {
        holderItemViewHolder.onBind(getItem(position))
    }

    inner class RouteItemViewHolder(
        viewBinding: ItemRouteListBinding,
    ) : BaseViewHolder<ItemRouteListBinding>(viewBinding) {

        fun onBind(route: TransportRoute) {
            viewBinding.apply {
                routeNumberContainer.apply {
                    when (route) {
                        is LrtTransportRoute -> {
                            val color = route.getColor(false).colorInt()
                            routeLRTNumberTv.apply {
                                text = route.routeNo
                                visible()

                                (background as? LayerDrawable)?.apply {
                                    mutate()
                                    (findDrawableByLayerId(R.id.item_line_color) as? GradientDrawable)?.apply {
                                        setStroke(dpToPx(3), color)
                                    }
                                }
                            }
                        }
                        is MtrTransportRoute -> {
                            val color = route.getColor(false).colorInt()
                            routeCompanyColor.setBackgroundColor(color)
                            routeBusNumberTv.apply {
                                text = route.getCardRouteText()
                                visible()
                            }
                        }
                        else -> {
                            routeBusNumberTv.apply {
                                text = route.getCardRouteText()
                                visible()
                            }
                        }
                    }
                }

                routeOrigTv.text = route.getLocalisedOrig(context)
                routeDestTv.text = route.getLocalisedDest(context)

                routeSpTv.apply {
                    if (route.isSpecialRoute()) {
                        text = context.getString(
                            R.string.text_add_eta_destination_sp_mobile,
                            route.serviceType
                        )
                        visible()
                    } else {
                        gone()
                    }
                }

                root.tag = route
                root.setOnClickListener { onRouteSelected(route) }
            }
        }

        fun getDirectionText(context: Context, route: TransportRoute): String {
            return context.getString(
                R.string.text_add_eta_destination_mobile,
                route.getLocalisedOrig(context),
                route.getLocalisedDest(context)
            )
        }
    }
}
