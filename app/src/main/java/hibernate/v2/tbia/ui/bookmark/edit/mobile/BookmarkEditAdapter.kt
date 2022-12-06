package hibernate.v2.tbia.ui.bookmark.edit.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.model.Card
import hibernate.v2.tbia.databinding.ItemBookmarkEditBinding
import hibernate.v2.tbia.ui.base.ItemTouchHelperAdapter

class BookmarkEditAdapter(
    private val onItemClick: (card: Card.SettingsEtaItemCard) -> Unit,
    private val onItemMoveCallback: (fromPosition: Int, toPosition: Int) -> Unit,
    private val requestDrag: (viewHolder: BookmarkEditViewHolder) -> Unit,
) : RecyclerView.Adapter<BookmarkEditViewHolder>(), ItemTouchHelperAdapter {

    private var list = mutableListOf<Card.SettingsEtaItemCard>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        BookmarkEditViewHolder {
        return BookmarkEditViewHolder(
            ItemBookmarkEditBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick
        ).apply {
            viewBinding.orderCl.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    requestDrag(this)
                }
                false
            }
        }
    }

    override fun onBindViewHolder(holder: BookmarkEditViewHolder, position: Int) {
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
        onItemMoveCallback(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {}
}
