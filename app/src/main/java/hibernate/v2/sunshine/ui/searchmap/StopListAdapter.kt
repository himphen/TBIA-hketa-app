package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.databinding.ItemBottomSheetStopBinding
import hibernate.v2.sunshine.databinding.ItemRouteBadgeBinding
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.searchmap.item.RouteBadge
import hibernate.v2.sunshine.ui.view.setStopRouteBadgeFlexManager
import hibernate.v2.sunshine.util.GeneralUtils.getTransportationLanguage
import hibernate.v2.sunshine.util.updateBackgroundColor
import hibernate.v2.utils.colorInt

class StopListAdapter(val onStopSelected: (SearchMapStop) -> Unit) :
    ListAdapter<SearchMapStop, StopListAdapter.ItemVH>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<SearchMapStop>() {
        override fun areItemsTheSame(oldItem: SearchMapStop, newItem: SearchMapStop): Boolean {
            return oldItem.stopId == newItem.stopId
        }

        override fun areContentsTheSame(oldItem: SearchMapStop, newItem: SearchMapStop): Boolean {
            return oldItem == newItem
        }
    }

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
        val item = getItem(position)
        holder.viewBinding.apply {
            stopCompanyColor.setBackgroundColor(item.etaType.color().colorInt(holder.context))
            stopNameTv.text = item.getLocalisedName(getTransportationLanguage(holder.context))

            (routeNumberRecyclerView.adapter as RouteBadgeAdapter).apply {
                setData(
                    item.mapRouteList
                        .map { RouteBadge(it.route.routeNo, it.route.getColor().colorInt()) }
                )
            }

            root.tag = item
            root.setOnClickListener { onStopSelected(it.tag as SearchMapStop) }
        }
    }

    inner class ItemVH(val viewBinding: ItemBottomSheetStopBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        val context: Context
            get() {
                return viewBinding.root.context
            }
    }

    inner class RouteBadgeAdapter : RecyclerView.Adapter<RouteBadgeAdapter.ItemStopBadgeVH>() {

        private var list = listOf<RouteBadge>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ItemStopBadgeVH(
                ItemRouteBadgeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: ItemStopBadgeVH, position: Int) {
            val item = list[position]
            holder.viewBinding.routeNumberTv.text = item.text

            holder.viewBinding.root.apply {
                updateBackgroundColor(item.color)
            }
        }

        override fun getItemCount() = list.size

        @SuppressLint("NotifyDataSetChanged")
        fun setData(list: List<RouteBadge>?) {
            if (list == null) return

            this.list = list
            notifyDataSetChanged()
        }

        inner class ItemStopBadgeVH(viewBinding: ItemRouteBadgeBinding) :
            BaseViewHolder<ItemRouteBadgeBinding>(viewBinding)
    }
}
