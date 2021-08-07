package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaStopBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.ui.base.BaseViewHolder

class AddEtaStopAdapter(
    private val listener: ItemListener,
) : RecyclerView.Adapter<AddEtaStopAdapter.AddEtaViewHolderStop>() {

    interface ItemListener {
        fun onStopSelected(card: Card.RouteStopAddCard)
    }

    private var list = mutableListOf<Card.RouteStopAddCard>()

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
    fun setStopData(mutableList: List<Card.RouteStopAddCard>?) {
        if (mutableList == null) return

        list.clear()
        list.addAll(mutableList)
        notifyDataSetChanged()
    }

    inner class AddEtaViewHolderStop(
        viewBinding: ItemAddEtaStopBinding,
    ) : BaseViewHolder<ItemAddEtaStopBinding>(viewBinding) {

        fun onBind(card: Card.RouteStopAddCard, isFirst: Boolean, isLast: Boolean) {
            viewBinding.stopNameTv.text = card.stop.nameTc
            viewBinding.stopSeqTv.text = String.format("%02d", card.stop.seq)

            when (card.route.company) {
                Company.UNKNOWN -> {
                }
                Company.KMB -> {
                    viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_kmb)
                    viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_kmb)
                    viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_kmb)
                }
                Company.NWFB -> {
                    viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_nwfb)
                    viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_nwfb)
                    viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_nwfb)
                }
                Company.CTB -> {
                    viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_ctb)
                    viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_ctb)
                    viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_ctb)
                }
                Company.GMB -> {
                    viewBinding.stopLineTop.setBackgroundResource(R.color.brand_color_gmb)
                    viewBinding.stopLineMiddle.setBackgroundResource(R.color.brand_color_gmb)
                    viewBinding.stopLineBottom.setBackgroundResource(R.color.brand_color_gmb)
                }
                Company.MTR -> {
                    val route = card.route as MTRTransportRoute
                    viewBinding.stopLineTop.setBackgroundColor(route.routeInfo.color)
                    viewBinding.stopLineMiddle.setBackgroundColor(route.routeInfo.color)
                    viewBinding.stopLineBottom.setBackgroundColor(route.routeInfo.color)
                }
            }

            viewBinding.stopLineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
            viewBinding.stopLineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

            viewBinding.root.tag = card
            viewBinding.root.setOnClickListener { listener.onStopSelected(card) }
        }
    }
}