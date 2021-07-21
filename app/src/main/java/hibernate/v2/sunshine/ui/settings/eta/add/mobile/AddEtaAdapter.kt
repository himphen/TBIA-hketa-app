package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemAddEtaCompanyBinding
import hibernate.v2.sunshine.databinding.ItemAddEtaRouteBinding
import hibernate.v2.sunshine.databinding.ItemAddEtaStopBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.view.AddEtaViewHolderCompany
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.view.AddEtaViewHolderRoute
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.view.AddEtaViewHolderStop
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.view.BaseAddEtaViewHolder

class AddEtaAdapter(
    private val type: AddEtaFragment.SelectionType,
    private val listener: ItemListener
) : RecyclerView.Adapter<BaseAddEtaViewHolder<out ViewBinding>>() {

    interface ItemListener {
        fun onEtaTypeSelected(etaType: AddEtaViewModel.EtaType)
        fun onRouteSelected(route: RouteForRowAdapter)
        fun onStopSelected(card: Card.RouteStopAddCard)
    }

    private var list = mutableListOf<AddEtaViewModel.EtaType>()
    private var list2 = mutableListOf<RouteForRowAdapter>()
    private var list3 = mutableListOf<Card.RouteStopAddCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseAddEtaViewHolder<out ViewBinding> {
        return when (type) {
            AddEtaFragment.SelectionType.EtaType -> AddEtaViewHolderCompany(
                ItemAddEtaCompanyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            AddEtaFragment.SelectionType.Route -> AddEtaViewHolderRoute(
                ItemAddEtaRouteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            AddEtaFragment.SelectionType.Stop -> AddEtaViewHolderStop(
                ItemAddEtaStopBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
        }
    }

    override fun onBindViewHolder(holder: BaseAddEtaViewHolder<out ViewBinding>, position: Int) {
        when (holder) {
            is AddEtaViewHolderCompany -> holder.onBind(list[position])
            is AddEtaViewHolderRoute -> holder.onBind(list2[position])
            is AddEtaViewHolderStop -> {
                val isFirst = position == 0
                val isLast = position == list3.lastIndex
                holder.onBind(list3[position], isFirst, isLast)
            }
        }
    }

    override fun getItemCount(): Int = when (type) {
        AddEtaFragment.SelectionType.EtaType -> list.size
        AddEtaFragment.SelectionType.Route -> list2.size
        AddEtaFragment.SelectionType.Stop -> list3.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTypeData(mutableList: List<AddEtaViewModel.EtaType>?) {
        if (mutableList == null) return

        list.clear()
        list.addAll(mutableList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRouteData(mutableList: List<RouteForRowAdapter>?) {
        if (mutableList == null) return

        list2.clear()
        list2.addAll(mutableList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStopData(mutableList: List<Card.RouteStopAddCard>?) {
        if (mutableList == null) return

        list3.clear()
        list3.addAll(mutableList)
        notifyDataSetChanged()
    }

    inner class AddEtaViewHolderStop(
        override val viewBinding: ItemAddEtaStopBinding,
        private val listener: AddEtaAdapter.ItemListener
    ) : BaseAddEtaViewHolder<ItemAddEtaStopBinding>(viewBinding) {

        fun onBind(card: Card.RouteStopAddCard, isFirst: Boolean, isLast: Boolean) {
            viewBinding.stopNameTv.text = card.stop.nameTc
            viewBinding.stopSeqTv.text = String.format("%02d", card.stop.seq)

            when (card.route.company) {
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
                Company.UNKNOWN -> {
                }
            }

            viewBinding.stopLineTop.visibility = if (isFirst) View.INVISIBLE else View.VISIBLE
            viewBinding.stopLineBottom.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

            viewBinding.root.tag = card
            viewBinding.root.setOnClickListener { listener.onStopSelected(card) }
        }
    }
}