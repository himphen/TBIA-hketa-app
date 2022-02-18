package hibernate.v2.sunshine.ui.bookmark.home.mobile.view

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ItemEtaCardCompactBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.bookmark.view.EtaRouteView
import hibernate.v2.sunshine.util.gone

class EtaViewHolderCompact(viewBinding: ItemEtaCardCompactBinding) :
    BaseEtaViewHolder<ItemEtaCardCompactBinding>(viewBinding),
    EtaRouteView {

    override fun onBind(card: Card.EtaCard) {
        viewBinding.content.apply {
            val route = card.route
            val color = route.getColor(context, false)

            (
                ContextCompat.getDrawable(
                    context,
                    R.drawable.eta_card_line_arrow
                ) as? RotateDrawable?
                ).let { arrowDrawable ->
                arrowDrawable?.mutate()
                (arrowDrawable?.drawable as? GradientDrawable)?.setColor(color)
                lineArrowBgView.background = arrowDrawable
                lineBgView.setBackgroundColor(color)
            }

            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = route.getDestDirectionText(context)

            val first = card.etaList.getOrNull(0)?.getEtaMinuteText()
            val second = card.etaList.getOrNull(1)?.getEtaMinuteText()
            val third = card.etaList.getOrNull(2)?.getEtaMinuteText()

            var etaMinuteList = mutableListOf(first, second, third)
            when (listOfNotNull(first, second, third).size) {
                1 -> {
                    etaMinuteList.add(0, null)
                    etaMinuteList.add(1, null)
                }
                2 -> {
                    etaMinuteList.add(0, null)
                }
            }
            etaMinuteList = etaMinuteList.slice(0..2).toMutableList()

            etaMinuteList.getOrNull(0)?.let {
                eta1MinuteTv.text = it.second
                eta1UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta1MinuteTv.text = ""
                eta1UnitTv.gone()
            }

            etaMinuteList.getOrNull(1)?.let {
                eta2MinuteTv.text = it.second
                eta2UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta2MinuteTv.text = ""
                eta2UnitTv.gone()
            }

            etaMinuteList.getOrNull(2)?.let {
                eta3MinuteTv.text = it.second
                eta3UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta3MinuteTv.text = "-"
                eta3UnitTv.gone()
            }
        }
    }
}
