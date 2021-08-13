package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import hibernate.v2.sunshine.databinding.ItemBottomSheetRouteBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.EtaTimeAdapter
import hibernate.v2.sunshine.ui.eta.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class RouteListAdapter(val onRouteSelected: (Card.EtaCard) -> Unit) :
    RecyclerView.Adapter<RouteListAdapter.ItemVH>() {

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemVH(
            ItemBottomSheetRouteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ).apply {
            viewBinding.etaTimeRv.apply {
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexWrap = FlexWrap.WRAP
                    alignItems = AlignItems.FLEX_END
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_END
                }
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
                root.setOnClickListener { onRouteSelected(it.tag as Card.EtaCard) }

                (etaTimeRv.adapter as EtaTimeAdapter).apply {
                    setData(card.etaList)
                }

                card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                    etaMinuteTv.text = it.second

                    if (it.first) {
                        etaMinuteUnitTv.visible()
                    } else {
                        etaMinuteTv.text = "-"
                        etaMinuteUnitTv.gone()
                    }
                } ?: run {
                    etaMinuteTv.text = ""
                    etaMinuteUnitTv.gone()
                }
            }
        }
    }
}