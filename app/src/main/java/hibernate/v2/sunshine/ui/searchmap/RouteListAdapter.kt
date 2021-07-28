package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.searchmap.Route

class RouteListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<RouteListAdapter.ItemStopMapVH>() {

    interface ItemListener {
        fun onRouteSelected(mapRoute: Route)
    }

    private var list = mutableListOf<Route>()

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
        val item = list[position]
        val route = item.route
        val context = viewBinding.root.context
        viewBinding.apply {
            routeNumberTv.text = route.routeNo
            routeDirectionTv.text = route.getDestDirectionText(context)
            routeCompanyColor.setBackgroundResource(route.getColor(true))
            root.tag = item
            root.setOnClickListener { listener.onRouteSelected(it.tag as Route) }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<Route>?) {
        if (list == null) return

        this.list = list
        notifyDataSetChanged()
    }

    fun replace(position: Int, item: Route) {
        list[position] = item
        notifyItemChanged(position)
    }

    inner class ItemStopMapVH(val viewBinding: ItemBottomSheetRouteBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}