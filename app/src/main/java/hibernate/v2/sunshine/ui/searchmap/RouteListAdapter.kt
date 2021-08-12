package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

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
            val color = route.getColor(context, false)

            viewBinding.apply {
                routeNumberTv.text = route.routeNo
                routeDirectionTv.text = route.getDestDirectionText(context)
                routeCompanyColor.setBackgroundColor(color)
                root.tag = card
                root.setOnClickListener { listener.onRouteSelected(it.tag as Card.EtaCard) }
                card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                    etaMinuteTv.text = it.second

                    if (it.first) {
                        etaMinuteUnitTv.visible()
                        etaTimeTv.text = getEtaTimeText(card.etaList)
                        etaTimeTv.visible()
                    } else {
                        etaMinuteTv.text = "-"
                        etaMinuteUnitTv.gone()
                        etaTimeTv.gone()
                    }
                } ?: run {
                    etaMinuteTv.text = ""
                    etaMinuteUnitTv.gone()
                    etaTimeTv.gone()
                }
            }
        }
    }
}