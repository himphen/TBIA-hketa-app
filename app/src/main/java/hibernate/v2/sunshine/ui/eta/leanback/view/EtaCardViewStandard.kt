package hibernate.v2.sunshine.ui.eta.leanback.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardEtaStandardBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class EtaCardViewStandard(context: Context) : BaseEtaCardView<CardEtaStandardBinding>(context) {
    override var viewBinding =
        CardEtaStandardBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
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
            viewBinding.lineArrowBgView.background = arrowDrawable
            viewBinding.lineBgView.setBackgroundResource(color)
        }

        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text = card.route.getDestDirectionText(context)

        getEtaMinuteText(card.etaList.getOrNull(0))?.let {
            viewBinding.eta1MinuteTv.text = it
            viewBinding.eta1UnitTv.visibility = View.VISIBLE
        } ?: run {
            viewBinding.eta1MinuteTv.text = "-"
            viewBinding.eta1UnitTv.visibility = View.GONE
        }

        getEtaMinuteText(card.etaList.getOrNull(1))?.let {
            viewBinding.eta2MinuteTv.text = it
            viewBinding.eta2UnitTv.visibility = View.VISIBLE
        } ?: run {
            viewBinding.eta2MinuteTv.text = "-"
            viewBinding.eta2UnitTv.visibility = View.GONE
        }

        getEtaMinuteText(card.etaList.getOrNull(2))?.let {
            viewBinding.eta3MinuteTv.text = it
            viewBinding.eta3UnitTv.visibility = View.VISIBLE
        } ?: run {
            viewBinding.eta3MinuteTv.text = "-"
            viewBinding.eta3UnitTv.visibility = View.GONE
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

    fun getEtaMinuteText(eta: TransportEta?): String? {
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