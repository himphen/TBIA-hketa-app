package hibernate.v2.sunshine.ui.eta.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.databinding.CardEtaClassicBinding
import hibernate.v2.sunshine.databinding.CardEtaCompactBinding
import hibernate.v2.sunshine.databinding.CardEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.leanback.view.EtaCardPresenter

class EtaCardAdapter(
    private val type: EtaCardPresenter.CardViewType
) : RecyclerView.Adapter<BaseEtaCardViewHolder<out ViewBinding>>() {

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseEtaCardViewHolder<out ViewBinding> {
        return when (type) {
            EtaCardPresenter.CardViewType.Classic -> EtaCardViewHolderClassic(
                CardEtaClassicBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardPresenter.CardViewType.Compact -> EtaCardViewHolderCompact(
                CardEtaCompactBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardPresenter.CardViewType.Standard -> EtaCardViewHolderStandard(
                CardEtaStandardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: BaseEtaCardViewHolder<out ViewBinding>, position: Int) {
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