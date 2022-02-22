package hibernate.v2.sunshine.ui.bookmark

import android.annotation.SuppressLint
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.sunshine.databinding.ItemEtaTimeBinding
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.util.DateFormatPattern
import hibernate.v2.sunshine.util.DateUtil.formatString

class EtaTimeAdapter : RecyclerView.Adapter<BaseViewHolder<ItemEtaTimeBinding>>() {

    private var list = listOf<TransportEta>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = BaseViewHolder(
        ItemEtaTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemEtaTimeBinding>,
        position: Int
    ) {
        val item = list[position]
        val etaTime = if (DateFormat.is24HourFormat(holder.context)) {
            item.eta.formatString(DateFormatPattern.HH_MM.value).uppercase()
        } else {
            item.eta.formatString(DateFormatPattern.HH_MM_12.value).uppercase()
        }
        holder.viewBinding.etaTimeTv.text = etaTime.trim()
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<TransportEta>?) {
        if (list == null) {
            this.list = emptyList()
            notifyDataSetChanged()
            return
        }

        this.list = list
        notifyDataSetChanged()
    }
}
