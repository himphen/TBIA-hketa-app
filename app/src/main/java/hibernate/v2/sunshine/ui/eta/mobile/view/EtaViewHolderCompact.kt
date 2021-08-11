package hibernate.v2.sunshine.ui.eta.mobile.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaCompactBinding
import hibernate.v2.sunshine.model.Card

class EtaViewHolderCompact(viewBinding: ItemEtaCompactBinding) :
    BaseEtaViewHolder<ItemEtaCompactBinding>(viewBinding) {

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
            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = route.getDestDirectionText(context)

            card.etaList.getOrNull(0)?.getEtaMinuteText()?.let {
                eta1MinuteTv.text = it.second
                eta1UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta1MinuteTv.text = "-"
                eta1UnitTv.visibility = View.GONE
            }

            card.etaList.getOrNull(1)?.getEtaMinuteText()?.let {
                eta2MinuteTv.text = it.second
                eta2UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta2MinuteTv.text = "-"
                eta2UnitTv.visibility = View.GONE
            }

            card.etaList.getOrNull(2)?.getEtaMinuteText()?.let {
                eta3MinuteTv.text = it.second
                eta3UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta3MinuteTv.text = "-"
                eta3UnitTv.visibility = View.GONE
            }
        }
    }
}