package hibernate.v2.sunshine.ui.eta.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.databinding.ContentEtaClassicBinding
import hibernate.v2.sunshine.databinding.ContentEtaCompactBinding
import hibernate.v2.sunshine.databinding.ContentEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.leanback.EtaCardPresenter
import hibernate.v2.sunshine.ui.eta.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderClassic
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderCompact
import hibernate.v2.sunshine.ui.eta.mobile.view.EtaViewHolderStandard

class EtaCardAdapter(
    private val type: EtaCardPresenter.CardViewType
) : RecyclerView.Adapter<BaseEtaViewHolder<out ViewBinding>>() {

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            BaseEtaViewHolder<out ViewBinding> {
        return when (type) {
            EtaCardPresenter.CardViewType.Classic -> EtaViewHolderClassic(
                ContentEtaClassicBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardPresenter.CardViewType.Compact -> EtaViewHolderCompact(
                ContentEtaCompactBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            EtaCardPresenter.CardViewType.Standard -> EtaViewHolderStandard(
                ContentEtaStandardBinding.inflate(
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