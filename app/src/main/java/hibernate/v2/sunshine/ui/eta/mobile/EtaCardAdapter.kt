package hibernate.v2.sunshine.ui.eta.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.databinding.ItemEtaClassicBinding
import hibernate.v2.sunshine.databinding.ItemEtaCompactBinding
import hibernate.v2.sunshine.databinding.ItemEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.ui.eta.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderClassic
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderCompact
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderStandard

class EtaCardAdapter(
    var type: EtaCardViewType,
) : RecyclerView.Adapter<BaseEtaViewHolder<out ViewBinding>>() {

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseEtaViewHolder<out ViewBinding> {
        return when (type) {
            EtaCardViewType.Classic -> EtaViewHolderClassic(
                ItemEtaClassicBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardViewType.Compact -> EtaViewHolderCompact(
                ItemEtaCompactBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardViewType.Standard -> EtaViewHolderStandard(
                ItemEtaStandardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseEtaViewHolder<out ViewBinding>, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(etaCardList: MutableList<Card.EtaCard>?) {
        if (etaCardList == null) return

        this.list = etaCardList
        notifyDataSetChanged()
    }

    fun replace(position: Int, etaCard: Card.EtaCard) {
        list[position] = etaCard
        notifyItemChanged(position)
    }
}