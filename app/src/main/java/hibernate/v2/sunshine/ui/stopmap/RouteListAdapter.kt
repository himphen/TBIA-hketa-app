package hibernate.v2.sunshine.ui.stopmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.transport.TransportRoute

class RouteListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<RouteListAdapter.ItemStopMapVH>() {

    interface ItemListener {
        fun onRouteSelected(route: TransportRoute)
    }

    private var list = mutableListOf<TransportRoute>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemStopMapVH(
            ItemBottomSheetRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemStopMapVH, position: Int) {
        val viewBinding = holder.viewBinding
        val route = list[position]
        val context = viewBinding.root.context
        viewBinding.apply {
            routeNumberTv.text = route.routeNo
            routeDirectionTv.text = route.getDestDirectionText(context)
            routeCompanyColor.setBackgroundResource(route.getColor(true))
            root.tag = route
            root.setOnClickListener { listener.onRouteSelected(it.tag as TransportRoute) }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(stopMapList: MutableList<TransportRoute>?) {
        if (stopMapList == null) return

        this.list = stopMapList
        notifyDataSetChanged()
    }

    fun replace(position: Int, transportRoute: TransportRoute) {
        list[position] = transportRoute
        notifyItemChanged(position)
    }

    inner class ItemStopMapVH(val viewBinding: ItemBottomSheetRouteBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}