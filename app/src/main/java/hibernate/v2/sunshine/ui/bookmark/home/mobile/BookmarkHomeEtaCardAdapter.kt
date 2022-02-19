package hibernate.v2.sunshine.ui.bookmark.home.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.databinding.ItemEtaButtonGroupBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardClassicBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardCompactBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.bookmark.EtaCardViewType
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderClassic
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderCompact
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderStandard
import hibernate.v2.sunshine.ui.view.setEtaTimeFlexManager

class BookmarkHomeEtaCardAdapter(
    var type: EtaCardViewType,
    val onAddButtonClick: () -> Unit,
    val onEditButtonClick: () -> Unit,
) : RecyclerView.Adapter<BaseViewHolder<out ViewBinding>>() {

    companion object {
        const val VIEW_TYPE_CONTENT = 1
        const val VIEW_TYPE_BUTTON_GROUP = 2
    }

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        BaseViewHolder<out ViewBinding> {
        return if (viewType == VIEW_TYPE_CONTENT) {
            when (type) {
                EtaCardViewType.Classic -> EtaViewHolderClassic(
                    ItemEtaCardClassicBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).apply {
                    viewBinding.content.etaMinuteLl.etaTimeRv.apply {
                        setEtaTimeFlexManager()
                        adapter = EtaTimeAdapter()
                    }
                }
                EtaCardViewType.Compact -> EtaViewHolderCompact(
                    ItemEtaCardCompactBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
                EtaCardViewType.Standard -> EtaViewHolderStandard(
                    ItemEtaCardStandardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        } else {
            EtaViewHolderButtonGroup(
                ItemEtaButtonGroupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ).apply {
                viewBinding.apply {
                    addStopButton.setOnClickListener {
                        onAddButtonClick()
                    }
                    editStopButton.setOnClickListener {
                        onEditButtonClick()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out ViewBinding>, position: Int) {
        if (holder is BaseEtaViewHolder)
            holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size + 1

    override fun getItemViewType(position: Int): Int =
        if (list.lastIndex + 1 == position) VIEW_TYPE_BUTTON_GROUP else VIEW_TYPE_CONTENT

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

    inner class EtaViewHolderButtonGroup(viewBinding: ItemEtaButtonGroupBinding) :
        BaseViewHolder<ItemEtaButtonGroupBinding>(viewBinding)
}