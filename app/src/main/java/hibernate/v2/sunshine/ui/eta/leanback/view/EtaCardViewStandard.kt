package hibernate.v2.sunshine.ui.eta.leanback.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.view.EtaRouteView
import hibernate.v2.sunshine.util.gone

class EtaCardViewStandard(context: Context) :
    BaseEtaCardView<ContentEtaStandardBinding>(context),
    EtaRouteView {

    override var viewBinding =
        ContentEtaStandardBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
        viewBinding.apply {
            val color = when (card.route.company) {
                Company.KMB -> R.color.brand_color_kmb
                Company.NWFB -> R.color.brand_color_nwfb
                Company.CTB -> R.color.brand_color_ctb
                Company.GMB -> R.color.brand_color_gmb
                else -> R.color.brand_color_kmb
            }

            (ContextCompat.getDrawable(
                context,
                R.drawable.eta_card_line_arrow
            ) as? RotateDrawable?).let { arrowDrawable ->
                arrowDrawable?.mutate()
                (arrowDrawable?.drawable as? GradientDrawable)?.setColor(context.getColor(color))
                lineArrowBgView.background = arrowDrawable
                lineBgView.setBackgroundResource(color)
            }

            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getName(context)
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

        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundResource(R.drawable.eta_card_bg_none_selected)
            } else {
                setBackgroundResource(R.drawable.eta_card_bg_none)
            }
        }
        setBackgroundResource(R.drawable.eta_card_bg_none)
    }
}