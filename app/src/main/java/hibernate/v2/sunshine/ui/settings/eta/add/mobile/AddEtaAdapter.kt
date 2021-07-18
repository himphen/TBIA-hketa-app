package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
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
            is AddEtaViewHolderStop -> holder.onBind(list3[position])
        }
    }

    override fun getItemCount(): Int = when (type) {
        AddEtaFragment.SelectionType.EtaType -> list.size
        AddEtaFragment.SelectionType.Route -> list2.size
        AddEtaFragment.SelectionType.Stop -> list3.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTypeData(mutableList: MutableList<AddEtaViewModel.EtaType>?) {
        if (mutableList == null) return

        list.clear()
        list.addAll(mutableList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRouteData(mutableList: MutableList<RouteForRowAdapter>?) {
        if (mutableList == null) return

        list2.clear()
        list2.addAll(mutableList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStopData(mutableList: MutableList<Card.RouteStopAddCard>?) {
        if (mutableList == null) return

        list3.clear()
        list3.addAll(mutableList)
        notifyDataSetChanged()
    }
}