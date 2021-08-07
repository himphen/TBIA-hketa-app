package hibernate.v2.sunshine.ui.eta.mobile.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class EtaViewHolderStandard(viewBinding: ItemEtaStandardBinding) :
    BaseEtaViewHolder<ItemEtaStandardBinding>(viewBinding) {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(context, false)

            (ContextCompat.getDrawable(
                context,
                R.drawable.eta_card_line_arrow
            ) as? RotateDrawable?).let { arrowDrawable ->
                arrowDrawable?.mutate()
                (arrowDrawable?.drawable as? GradientDrawable)?.setColor(color)
                lineArrowBgView.background = arrowDrawable
                lineBgView.setBackgroundColor(color)
            }

            routeNumberTv.apply { text = card.route.getCardRouteText() }

            stopNameTv.text = card.stop.nameTc
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            getEtaMinuteText(card.etaList.getOrNull(0))?.let {
                eta1MinuteTv.text = it
                eta1UnitTv.visibility = View.VISIBLE
            } ?: run {
                eta1MinuteTv.text = "-"
                eta1UnitTv.visibility = View.GONE
            }

            getEtaMinuteText(card.etaList.getOrNull(1))?.let {
                eta2MinuteTv.text = it
                eta2UnitTv.visibility = View.VISIBLE
            } ?: run {
                eta2MinuteTv.text = "-"
                eta2UnitTv.visibility = View.GONE
            }

            getEtaMinuteText(card.etaList.getOrNull(2))?.let {
                eta3MinuteTv.text = it
                eta3UnitTv.visibility = View.VISIBLE
            } ?: run {
                eta3MinuteTv.text = "-"
                eta3UnitTv.visibility = View.GONE
            }
        }
    }

    private fun getEtaMinuteText(eta: TransportEta?): String? {
        eta?.eta?.let { etaDate ->
            val minutes = DateUtil.getTimeDiffInMin(
                etaDate,
                Date()
            )
            return (minutes + 1).toString()
        }

        return null
    }
}