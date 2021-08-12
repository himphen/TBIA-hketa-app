package hibernate.v2.sunshine.ui.eta.edit.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemEditEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.ItemTouchHelperAdapter

class EditEtaAdapter(
    private val listener: ItemListener
) : RecyclerView.Adapter<EditEtaViewHolder>(), ItemTouchHelperAdapter {

    interface ItemListener {
        fun onItemClick(card: Card.SettingsEtaItemCard)
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun requestDrag(viewHolder: EditEtaViewHolder)
    }

    private var list = mutableListOf<Card.SettingsEtaItemCard>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            EditEtaViewHolder {
        return EditEtaViewHolder(
            ItemEditEtaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        ).apply {
            viewBinding.orderCl.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.requestDrag(this)
                }
                false
            }
        }
    }

    override fun onBindViewHolder(holder: EditEtaViewHolder, position: Int) {
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