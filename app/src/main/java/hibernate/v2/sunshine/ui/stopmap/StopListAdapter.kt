package hibernate.v2.sunshine.ui.stopmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemStopMapRouteBinding
import hibernate.v2.sunshine.model.transport.StopMap

class StopListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<StopListAdapter.ItemStopMapVH>() {

    interface ItemListener {
        fun onStopSelected(stop: StopMap)
    }

    private var list = mutableListOf<StopMap>()

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
        val stopMap = list[position]
        val context = viewBinding.root.context
        viewBinding.apply {
            routeNumberTv.text = stopMap.nameTc
            routeDirectionTv.text = stopMap.stopId
            root.tag = stopMap
            root.setOnClickListener { listener.onStopSelected(it.tag as StopMap) }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(stopMapList: MutableList<StopMap>?) {
        if (stopMapList == null) return

        this.list = stopMapList
        notifyDataSetChanged()
    }

    fun replace(position: Int, stopMap: StopMap) {
        list[position] = stopMap
        notifyItemChanged(position)
    }

    inner class ItemStopMapVH(val viewBinding: ItemStopMapRouteBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}