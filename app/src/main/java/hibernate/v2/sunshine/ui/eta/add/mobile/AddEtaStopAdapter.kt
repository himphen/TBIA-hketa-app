package hibernate.v2.sunshine.ui.eta.add.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemAddEtaStopBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.ui.base.BaseViewHolder

class AddEtaStopAdapter(
    val route: TransportRoute,
    private val onStopSelected: (Card.RouteStopAddCard) -> Unit,
) : RecyclerView.Adapter<AddEtaStopAdapter.AddEtaViewHolderStop>() {

    private var list = mutableListOf<TransportStop>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AddEtaViewHolderStop(
            ItemAddEtaStopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AddEtaViewHolderStop, position: Int) {
        val isFirst = position == 0
        val isLast = position == list.lastIndex
        holder.onBind(list[position], isFirst, isLast)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setStopData(mutableList: List<TransportStop>?) {
        if (mutableList == null) return

        list.clear()
        list.addAll(mutableList)
        notifyDataSetChanged()
    }

    inner class AddEtaViewHolderStop(
        viewBinding: ItemAddEtaStopBinding,
    ) : BaseViewHolder<ItemAddEtaStopBinding>(viewBinding) {

        fun onBind(stop: TransportStop, isFirst: Boolean, isLast: Boolean) {
            val color = route.getColor(context)

            viewBinding.apply {
                stopNameTv.text = stop.getName(context)
                stopSeqTv.text = String.format("%02d", stop.seq)

                stopLineTop.setBackgroundColor(color)
                stopLineMiddle.setBackgroundColor(color)
                stopLineBottom.setBackgroundColor(color)

                stopLineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
                stopLineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

                root.tag = stop
                root.setOnClickListener {
                    onStopSelected(
                        Card.RouteStopAddCard(
                            route = route,
                            stop = stop
                        )
                    )
                }
            }
        }
    }
}
