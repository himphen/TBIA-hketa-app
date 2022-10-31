package hibernate.v2.sunshine.ui.route.details.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import hibernate.v2.model.Card
import hibernate.v2.model.transport.RouteDetailsStop
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.sunshine.databinding.ItemRouteDetailsStopBinding
import hibernate.v2.sunshine.databinding.ItemRouteDetailsStopExpandedBinding
import hibernate.v2.sunshine.model.getColor
import hibernate.v2.sunshine.model.getLocalisedName
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.view.setEtaTimeFlexManager
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.invisible
import hibernate.v2.sunshine.util.visible

class RouteDetailsAdapter(
    val route: TransportRoute,
    private val onItemExpanding: (RouteDetailsStop) -> Unit,
    private val onItemCollapsing: () -> Unit,
    private val onAddButtonClicked: (Int, Card.RouteStopAddCard) -> Unit,
    private val onRemoveButtonClicked: (Int, Int) -> Unit,
    private val onNavigationButtonClicked: (RouteDetailsStop) -> Unit,
    private val onStreetViewButtonClicked: (RouteDetailsStop) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_NORMAL = 1
        const val VIEW_TYPE_EXPANDED = 2
    }

    private var stopList = mutableListOf<RouteDetailsStop>()
    private var etaList: List<TransportEta>? = null
    var expandedPosition: Int = -1

    fun normalItemOnClickListener(item: RouteDetailsStop, position: Int) {
        etaList = null
        expandedPosition = position
        onItemExpanding(item)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_NORMAL -> {
                return ItemViewHolder(
                    ItemRouteDetailsStopBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return ExpandedItemViewHolder(
                    ItemRouteDetailsStopExpandedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).apply {
                    viewBinding.etaMinuteLl.etaTimeRv.apply {
                        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
                        setEtaTimeFlexManager()
                        adapter = EtaTimeAdapter()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.onBind(stopList[position])
            }
            is ExpandedItemViewHolder -> {
                holder.onBind(stopList[position])
            }
        }
    }

    override fun getItemCount(): Int = stopList.size

    override fun getItemViewType(position: Int): Int {
        return if (position != expandedPosition) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_EXPANDED
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRouteDetailsStopData(list: List<RouteDetailsStop>?) {
        if (list == null) return

        stopList.clear()
        stopList.addAll(list)
        notifyDataSetChanged()
    }

    fun setEtaData(list: List<TransportEta>?) {
        if (list == null) return

        etaList = list.toList()

        if (expandedPosition >= 0) {
            notifyItemChanged(expandedPosition)
        }
    }

    fun setSavedBookmark(position: Int, savedEtaId: Int) {
        stopList.getOrNull(position)?.let {
            it.savedEtaId = savedEtaId
            notifyItemChanged(position)
        }
    }

    fun setRemovedBookmark(position: Int) {
        stopList.getOrNull(position)?.let {
            it.savedEtaId = null
            notifyItemChanged(position)
        }
    }

    inner class ItemViewHolder(
        viewBinding: ItemRouteDetailsStopBinding,
    ) : BaseViewHolder<ItemRouteDetailsStopBinding>(viewBinding) {

        fun onBind(item: RouteDetailsStop) {
            val isFirst = absoluteAdapterPosition == 0
            val isLast = absoluteAdapterPosition == stopList.lastIndex
            val stop = item.transportStop
            val color = route.getColor(context)
            viewBinding.apply {
                contentRouteDetailsStop.stopNameTv.text = stop.getLocalisedName(context)
                contentRouteDetailsStop.stopSeqTv.text = String.format("%02d", stop.seq)

                contentRouteDetailsStop.stopLineTop.setBackgroundColor(color)
                contentRouteDetailsStop.stopLineMiddle.setBackgroundColor(color)
                contentRouteDetailsStop.stopLineBottom.setBackgroundColor(color)

                contentRouteDetailsStop.stopLineTop.visibility =
                    if (isFirst) View.INVISIBLE else View.VISIBLE
                contentRouteDetailsStop.stopLineBottom.visibility =
                    if (isLast) View.INVISIBLE else View.VISIBLE

                root.tag = stop
                root.setOnClickListener { normalItemOnClickListener(item, absoluteAdapterPosition) }
            }
        }
    }

    inner class ExpandedItemViewHolder(
        viewBinding: ItemRouteDetailsStopExpandedBinding,
    ) : BaseViewHolder<ItemRouteDetailsStopExpandedBinding>(viewBinding) {

        fun onBind(item: RouteDetailsStop) {
            val color = route.getColor(context)
            val isFirst = absoluteAdapterPosition == 0
            val isLast = absoluteAdapterPosition == stopList.lastIndex
            val stop = item.transportStop
            viewBinding.apply {
                contentRouteDetailsStop.stopNameTv.text = stop.getLocalisedName(context)
                contentRouteDetailsStop.stopSeqTv.text = String.format("%02d", stop.seq)

                contentRouteDetailsStop.stopLineTop.setBackgroundColor(color)
                contentRouteDetailsStop.stopLineMiddle.setBackgroundColor(color)
                contentRouteDetailsStop.stopLineBottom.setBackgroundColor(color)
                stopLineExpanded.setBackgroundColor(color)

                contentRouteDetailsStop.stopLineTop.visibility =
                    if (isFirst) View.INVISIBLE else View.VISIBLE
                contentRouteDetailsStop.stopLineBottom.visibility =
                    if (isLast) View.INVISIBLE else View.VISIBLE
                stopLineExpanded.visibility =
                    if (isLast) View.INVISIBLE else View.VISIBLE

                (etaMinuteLl.etaTimeRv.adapter as EtaTimeAdapter).apply {
                    val etaListWithoutFirstItem = etaList?.toMutableList()?.apply {
                        if (size > 0) removeAt(0)
                    }
                    setData(etaListWithoutFirstItem)
                }

                etaList?.let { etaList ->
                    etaMinuteLl.etaMinuteTv.visible()
                    etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                        etaMinuteLl.etaMinuteTv.text = it.second

                        if (it.first) {
                            etaMinuteLl.etaMinuteUnitTv.visible()
                        } else {
                            etaMinuteLl.etaMinuteUnitTv.gone()
                        }
                    } ?: run {
                        etaMinuteLl.etaMinuteTv.text = "-"
                        etaMinuteLl.etaMinuteUnitTv.gone()
                    }
                } ?: run {
                    etaMinuteLl.etaMinuteTv.invisible()
                    etaMinuteLl.etaMinuteUnitTv.gone()
                }

                bookmarkAddBtn.setOnClickListener {
                    onAddButtonClicked(
                        absoluteAdapterPosition,
                        Card.RouteStopAddCard(
                            route = route,
                            stop = stop
                        )
                    )
                }

                bookmarkRemoveBtn.setOnClickListener {
                    item.savedEtaId?.let { savedEtaId ->
                        onRemoveButtonClicked(
                            absoluteAdapterPosition,
                            savedEtaId
                        )
                    }
                }

                streetViewBtn.setOnClickListener {
                    onStreetViewButtonClicked(item)
                }

                navigationBtn.setOnClickListener {
                    onNavigationButtonClicked(item)
                }

                if (item.savedEtaId != null) {
                    bookmarkAddBtn.gone()
                    bookmarkRemoveBtn.visible()
                } else {
                    bookmarkAddBtn.visible()
                    bookmarkRemoveBtn.gone()
                }

                root.tag = stop
                root.setOnClickListener {
                    expandedPosition = -1
                    onItemCollapsing()
                    notifyItemChanged(absoluteAdapterPosition)
                }
            }
        }
    }
}
