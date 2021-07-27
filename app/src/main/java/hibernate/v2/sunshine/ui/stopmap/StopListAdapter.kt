package hibernate.v2.sunshine.ui.stopmap

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemBottomSheetStopBinding
import hibernate.v2.sunshine.databinding.ItemRouteBadgeBinding
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.StopMap
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.util.updateBackgroundColor

class StopListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<StopListAdapter.ItemStopMapVH>() {

    interface ItemListener {
        fun onStopSelected(stop: StopMap)
    }

    private var list = mutableListOf<StopMap>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemStopMapVH {
        return ItemStopMapVH(
            ItemBottomSheetStopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            viewBinding.routeNumberRecyclerView.apply {
                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL)
                    .apply {
                        setDrawable(
                            ContextCompat.getDrawable(context, R.drawable.space_horizontal)!!
                        )
                    }
                )
                adapter = RouteBadgeAdapter()
            }
        }
    }

    override fun onBindViewHolder(holder: ItemStopMapVH, position: Int) {
        val stopMap = list[position]
        holder.viewBinding.apply {
            stopCompanyColor.setBackgroundColor(stopMap.etaType.etaTypeColors(holder.context)[0])
            stopNameTv.text = stopMap.nameTc

            (routeNumberRecyclerView.adapter as RouteBadgeAdapter).apply {
                setType(holder.context, stopMap.etaType)
                setData(stopMap.routeNumberList.distinct())
            }

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

    inner class ItemStopMapVH(val viewBinding: ItemBottomSheetStopBinding) :
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
                val color = etaType.etaTypeColors(context)[0]
                updateBackgroundColor(color)
            }
        }

        override fun getItemCount() = list.size

        fun setType(context: Context, etaType: EtaType) {
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