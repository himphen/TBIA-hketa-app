package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetStopBinding
import hibernate.v2.sunshine.databinding.ItemRouteBadgeBinding
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.view.setStopRouteBadgeFlexManager
import hibernate.v2.sunshine.util.updateBackgroundColor

class StopListAdapter(val onStopSelected: (SearchMapStop) -> Unit) :
    RecyclerView.Adapter<StopListAdapter.ItemVH>() {

    private var list = mutableListOf<SearchMapStop>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        return ItemVH(
            ItemBottomSheetStopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            viewBinding.routeNumberRecyclerView.apply {
                setStopRouteBadgeFlexManager()
                adapter = RouteBadgeAdapter()
            }
        }
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        val item = list[position]
        holder.viewBinding.apply {
            stopCompanyColor.setBackgroundColor(item.etaType.color(holder.context))
            stopNameTv.text = item.nameTc

            (routeNumberRecyclerView.adapter as RouteBadgeAdapter).apply {
                setType(item.etaType)
                setData(
                    item.mapRouteList
                        .sortedBy { it.route }
                        .map { it.route.routeNo }
                        .distinct()
                )
            }

            root.tag = item
            root.setOnClickListener { onStopSelected(it.tag as SearchMapStop) }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<SearchMapStop>?) {
        if (list == null) return

        this.list = list
        notifyDataSetChanged()
    }

    fun replace(position: Int, item: SearchMapStop) {
        list[position] = item
        notifyItemChanged(position)
    }

    inner class ItemVH(val viewBinding: ItemBottomSheetStopBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        val context: Context
            get() {
                return viewBinding.root.context
            }
    }

    inner class RouteBadgeAdapter : RecyclerView.Adapter<RouteBadgeAdapter.ItemStopBadgeVH>() {

        private var list = listOf<String>()
        private var etaType: EtaType = EtaType.KMB

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStopBadgeVH {
            return ItemStopBadgeVH(
                ItemRouteBadgeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ItemStopBadgeVH, position: Int) {
            holder.viewBinding.routeNumberTv.text = list[position]

            holder.viewBinding.root.apply {
                updateBackgroundColor(etaType.color(context))
            }
        }

        override fun getItemCount() = list.size

        fun setType(etaType: EtaType) {
            this.etaType = etaType
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setData(list: List<String>?) {
            if (list == null) return

            this.list = list
            notifyDataSetChanged()
        }

        inner class ItemStopBadgeVH(viewBinding: ItemRouteBadgeBinding) :
            BaseViewHolder<ItemRouteBadgeBinding>(viewBinding)
    }
}
