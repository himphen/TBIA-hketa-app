package hibernate.v2.sunshine.ui.stopmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemStopMapRouteBinding
import hibernate.v2.sunshine.model.transport.TransportRoute

class StopMapAdapter : RecyclerView.Adapter<StopMapAdapter.ItemStopMapVH>() {

    private var list = mutableListOf<TransportRoute>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemStopMapVH(
            ItemStopMapRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemStopMapVH, position: Int) {
        val viewBinding = holder.viewBinding
        val route = list[position]
        val context = viewBinding.root.context
        viewBinding.routeNumberTv.text = route.routeNo
        viewBinding.routeDirectionTv.text = route.getDestDirectionText(context)
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

    inner class ItemStopMapVH(val viewBinding: ItemStopMapRouteBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}