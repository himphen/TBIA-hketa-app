package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.ui.bookmark.view.EtaRouteView
import hibernate.v2.sunshine.ui.view.setEtaTimeFlexManager
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class RouteListAdapter(val onRouteSelected: (Card.EtaCard) -> Unit) :
    RecyclerView.Adapter<RouteListAdapter.ItemVH>() {

    var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemVH(
            ItemBottomSheetRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            viewBinding.etaTimeRv.apply {
                setEtaTimeFlexManager()
                adapter = EtaTimeAdapter()
            }
        }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: MutableList<Card.EtaCard>?) {
        if (list == null) return

        this.list = list
        notifyDataSetChanged()
    }

    fun replace(position: Int, item: Card.EtaCard) {
        if (list.getOrNull(position) == null) return

        list[position] = item
        notifyItemChanged(position)
    }

    inner class ItemVH(viewBinding: ItemBottomSheetRouteBinding) :
        BaseEtaViewHolder<ItemBottomSheetRouteBinding>(viewBinding),
        EtaRouteView {

        override fun onBind(card: Card.EtaCard) {
            viewBinding.apply {
                applyRouteNumberContainer(card, routeNumberContainer)

                routeDirectionTv.text = card.route.getDestDirectionText(context)

                (etaTimeRv.adapter as EtaTimeAdapter).apply {
                    setData(card.etaList)
                }

                root.tag = card
                root.setOnClickListener { onRouteSelected(it.tag as Card.EtaCard) }

                card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                    etaMinuteTv.text = it.second

                    if (it.first) {
                        etaMinuteUnitTv.visible()
                    } else {
                        etaMinuteUnitTv.gone()
                    }
                } ?: run {
                    etaMinuteTv.text = "-"
                    etaMinuteUnitTv.gone()
                }
            }
        }
    }
}
