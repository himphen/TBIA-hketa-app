package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.searchmap.SearchMapRoute

class RouteListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<RouteListAdapter.ItemVH>() {

    interface ItemListener {
        fun onRouteSelected(searchMapRoute: SearchMapRoute)
    }

    private var list = mutableListOf<SearchMapRoute>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemVH(
            ItemBottomSheetRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        val viewBinding = holder.viewBinding
        val item = list[position]
        val route = item.route
        val context = viewBinding.root.context
        viewBinding.apply {
            routeNumberTv.text = route.routeNo
            routeDirectionTv.text = route.getDestDirectionText(context)
            routeCompanyColor.setBackgroundResource(route.getColor(true))
            root.tag = item
            root.setOnClickListener { listener.onRouteSelected(it.tag as SearchMapRoute) }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<SearchMapRoute>?) {
        if (list == null) return

        this.list = list
        notifyDataSetChanged()
    }

    fun replace(position: Int, item: SearchMapRoute) {
        list[position] = item
        notifyItemChanged(position)
    }

    inner class ItemVH(val viewBinding: ItemBottomSheetRouteBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}