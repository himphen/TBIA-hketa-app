package hibernate.v2.sunshine.ui.settings.eta.listing.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemSettingsEtaListingBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.ItemTouchHelperAdapter

class SettingsEtaListingAdapter(
    private val listener: ItemListener
) : RecyclerView.Adapter<SettingsEtaViewHolder>(), ItemTouchHelperAdapter {

    interface ItemListener {
        fun onItemClick(card: Card.SettingsEtaItemCard)

        fun onItemMove(fromPosition: Int, toPosition: Int)
    }

    private var list = mutableListOf<Card.SettingsEtaItemCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SettingsEtaViewHolder {
        return SettingsEtaViewHolder(
            ItemSettingsEtaListingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: SettingsEtaViewHolder, position: Int) {
        val item = list[position]
        holder.onBindItemView(item)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(etaCardList: MutableList<Card.SettingsEtaItemCard>?) {
        if (etaCardList == null) return

        this.list = etaCardList
        notifyDataSetChanged()
    }

    fun move(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    fun remove(position: Int) {
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        listener.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {}
}