package hibernate.v2.sunshine.ui.eta

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.leanback.widget.BaseCardView
import hibernate.v2.api.model.Eta
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseCardPresenter
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Date

class EtaCardPresenter(
    context: Context,
    private val fragmentWidth: Int
) : BaseCardPresenter<EtaCardView, Card.RouteEtaStopCard>(context) {

    override fun onCreateView(): EtaCardView {
        val cardView = EtaCardView(context)
        cardView.layoutParams = BaseCardView.LayoutParams(
            fragmentWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
//        cardView.viewBinding.root.layoutParams = BaseCardView.LayoutParams(
//            cardView.measuredWidth,
//            FrameLayout.LayoutParams.WRAP_CONTENT
//        )
        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setImageBackground(cardView, R.color.settings_card_background)
            } else {
                setImageBackground(cardView, R.color.settings_card_background)
            }
        }
        setImageBackground(cardView, R.color.settings_card_background)
        return cardView
    }

    override fun onBindViewHolder(card: Card.RouteEtaStopCard, cardView: EtaCardView) {
        cardView.tag = card
        val viewBinding = cardView.viewBinding

        viewBinding.routeIdTv.text = card.route.routeId
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text =
            context.getString(R.string.text_eta_destination, card.route.destTc)
        viewBinding.etaMinuteTv.text =
            getEtaMinuteText(card.etaList.getOrNull(0))
        viewBinding.etaTimeTv.text = getEtaTimeText(card.etaList)
    }

    private fun setImageBackground(cardView: EtaCardView, colorId: Int) {
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorId))
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
}