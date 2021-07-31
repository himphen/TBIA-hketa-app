package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.mobile.view.BaseEtaViewHolder

class RouteListAdapter(val listener: ItemListener) :
    RecyclerView.Adapter<RouteListAdapter.ItemVH>() {

    interface ItemListener {
        fun onRouteSelected(card: Card.EtaCard)
    }

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemVH(
            ItemBottomSheetRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

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
        list[position] = item
        notifyItemChanged(position)
    }

    inner class ItemVH(viewBinding: ItemBottomSheetRouteBinding) :
        BaseEtaViewHolder<ItemBottomSheetRouteBinding>(viewBinding) {

        override fun onBind(card: Card.EtaCard) {
            val route = card.route
            val color = route.getColor(false)

            viewBinding.apply {
                routeNumberTv.text = route.routeNo
                routeDirectionTv.text = route.getDestDirectionText(context)
                routeCompanyColor.setBackgroundResource(color)
                root.tag = card
                root.setOnClickListener { listener.onRouteSelected(it.tag as Card.EtaCard) }
                etaMinuteTv.text =
                    card.etaList.getOrNull(0)?.getEtaMinuteText(context)
                etaTimeTv.text = getEtaTimeText(card.etaList)
            }
        }
    }
}