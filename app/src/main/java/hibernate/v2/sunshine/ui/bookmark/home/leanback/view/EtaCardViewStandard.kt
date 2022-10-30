package hibernate.v2.sunshine.ui.bookmark.home.leanback.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.model.Card
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaStandardBinding
import hibernate.v2.sunshine.ui.bookmark.view.EtaRouteView
import hibernate.v2.sunshine.util.gone

class EtaCardViewStandard(context: Context) :
    BaseEtaCardView<ContentEtaStandardBinding>(context),
    EtaRouteView {

    override var viewBinding =
        ContentEtaStandardBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
        viewBinding.apply {
            val color = card.route.getColor(context)

            (
                ContextCompat.getDrawable(
                    context,
                    R.drawable.eta_card_line_arrow
                ) as? RotateDrawable?
                ).let { arrowDrawable ->
                arrowDrawable?.mutate()
                (arrowDrawable?.drawable as? GradientDrawable)?.setColor(color)
                lineBgView.setBackgroundColor(color)
            }

            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getLocalisedName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            card.etaList.getOrNull(0)?.getEtaMinuteText()?.let {
                eta1MinuteTv.text = it.second
                eta1UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta1MinuteTv.text = "-"
                eta1UnitTv.gone()
            }

            card.etaList.getOrNull(1)?.getEtaMinuteText()?.let {
                eta2MinuteTv.text = it.second
                eta2UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta2MinuteTv.text = "-"
                eta2UnitTv.gone()
            }

            card.etaList.getOrNull(2)?.getEtaMinuteText()?.let {
                eta3MinuteTv.text = it.second
                eta3UnitTv.visibility = if (it.first) View.VISIBLE else View.GONE
            } ?: run {
                eta3MinuteTv.text = "-"
                eta3UnitTv.gone()
            }
        }
    }
}
