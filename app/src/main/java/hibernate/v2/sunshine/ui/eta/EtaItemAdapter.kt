package hibernate.v2.sunshine.ui.eta

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ListItemEtaBinding
import hibernate.v2.sunshine.model.RouteEtaStop
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Date

class EtaItemAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val viewBinding = holder.viewBinding
        viewBinding.routeIdTv.text = item.route.routeId
        viewBinding.stopNameTv.text = item.stop.nameTc
        viewBinding.routeDirectionTv.text =
            context.getString(R.string.text_eta_destination, item.route.destTc)
        viewBinding.etaMinuteTv.text =
            getEtaMinuteText(item.etaList.getOrNull(0))
        viewBinding.etaTimeTv.text = getEtaTimeText(item.etaList)
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