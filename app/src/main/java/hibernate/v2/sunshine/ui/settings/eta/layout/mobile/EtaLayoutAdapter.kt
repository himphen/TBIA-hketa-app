package hibernate.v2.sunshine.ui.settings.eta.layout.mobile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemEtaLayoutSelectionBinding
import hibernate.v2.sunshine.ui.eta.leanback.EtaCardPresenter

class EtaLayoutAdapter(
    defaultType: EtaCardPresenter.CardViewType,
    private val listener: ItemListener,
) : RecyclerView.Adapter<EtaLayoutAdapter.ItemViewHolder>() {

    private var lastCheckedPosition: Int = when (defaultType) {
        EtaCardPresenter.CardViewType.Standard -> 0
        EtaCardPresenter.CardViewType.Compact -> 1
        EtaCardPresenter.CardViewType.Classic -> 2
    }

    interface ItemListener {
        fun onItemSelected(item: EtaCardPresenter.CardViewType)
    }

    private val list: MutableList<EtaCardPresenter.CardViewType> = mutableListOf(
        EtaCardPresenter.CardViewType.Standard,
        EtaCardPresenter.CardViewType.Compact,
        EtaCardPresenter.CardViewType.Classic
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemEtaLayoutSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val viewBinding = holder.viewBinding
        val context = viewBinding.root.context
        val item = list[position]
        viewBinding.radioButton.isChecked = position == lastCheckedPosition
        viewBinding.title.text = item.getTitle(context)
        viewBinding.root.tag = item
        viewBinding.root.setOnClickListener {
            listener.onItemSelected(it.tag as EtaCardPresenter.CardViewType)

            val copyOfLastCheckedPosition = lastCheckedPosition
            lastCheckedPosition = position
            notifyItemChanged(copyOfLastCheckedPosition)
            notifyItemChanged(lastCheckedPosition)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ItemViewHolder(val viewBinding: ItemEtaLayoutSelectionBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}