package hibernate.v2.sunshine.ui.eta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.api.model.Eta
import hibernate.v2.api.model.RouteEtaStop
import hibernate.v2.sunshine.databinding.ListItemEtaBinding
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Date

class EtaItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<RouteEtaStop>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ListItemEtaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        holder as ItemViewHolder
        val itemBinding = holder.viewBinding
        itemBinding.routeIdTv.text = item.route.route
        itemBinding.stopNameTv.text = item.stop.nameTc
        itemBinding.routeDirectionTv.text = item.route.destTc
        itemBinding.etaMinuteTv.text =
            getEtaMinuteText(item.etaList.getOrNull(0))
        itemBinding.etaTimeTv.text = getEtaTimeText(item.etaList)
    }

    private fun getEtaMinuteText(eta: Eta?): String {
        eta?.eta?.let { etaString ->
            val minutes = DateUtil.getTimeDiffInMin(
                DateUtil.getDate(etaString, DateFormat.ISO_WITHOUT_MS.value)!!,
                Date()
            )
            return (minutes + 1).toString() + " 分鐘"
        } ?: run {
            eta?.rmkTc?.let { rmkTc ->
                if (rmkTc.isNotEmpty()) return rmkTc
            }

            return "-"
        }
    }

    private fun getEtaTimeText(etaList: List<Eta>): String {
        var string = ""

        etaList.forEach {
            string += DateUtil.getDate(it.eta, DateFormat.ISO_WITHOUT_MS.value)
                .formatString(DateFormat.HH_MM.value) + "    "
        }

        return string.trim()
    }

    override fun getItemCount(): Int = list.size

    fun setData(list: List<RouteEtaStop>) {
        this.list = list
        notifyDataSetChanged()
    }

    internal class ItemViewHolder(val viewBinding: ListItemEtaBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}