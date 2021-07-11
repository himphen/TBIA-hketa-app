package hibernate.v2.sunshine.ui.eta

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.leanback.widget.BaseCardView
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseCardPresenter
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Date

class EtaCardPresenter(
    context: Context,
    private val fragmentWidth: Int
) : BaseCardPresenter<EtaCardView, Card.EtaCard>(
    ContextThemeWrapper(context, R.style.Theme_Fragment_Eta)
) {

    override fun onCreateView(): EtaCardView {
        val cardView = EtaCardView(ContextThemeWrapper(context, R.style.FullCardStyle))
        cardView.isFocusable = true
        cardView.layoutParams = BaseCardView.LayoutParams(
            fragmentWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.viewBinding.root.layoutParams = BaseCardView.LayoutParams(
            fragmentWidth,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        return cardView
    }

    override fun onBindViewHolder(card: Card.EtaCard, cardView: EtaCardView) {
        cardView.tag = card
        val viewBinding = cardView.viewBinding

        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.nameTc
        viewBinding.routeDirectionTv.text =
            context.getString(R.string.text_eta_destination, card.route.destTc)
        viewBinding.etaMinuteTv.text =
            getEtaMinuteText(card.etaList.getOrNull(0))
        viewBinding.etaTimeTv.text = getEtaTimeText(card.etaList)

        cardView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                when (card.route.company) {
                    Company.KMB -> cardView.setBackgroundResource(R.drawable.eta_card_bg_kmb_selected)
                    Company.NWFB, Company.CTB -> cardView.setBackgroundResource(R.drawable.eta_card_bg_ctb_selected)
                    else -> {
                    }
                }
            } else {
                setDefaultBackground(card, cardView)
            }
        }
        setDefaultBackground(card, cardView)
    }

    private fun setDefaultBackground(card: Card.EtaCard, cardView: EtaCardView) {
        when (card.route.company) {
            Company.KMB -> cardView.setBackgroundResource(R.drawable.eta_card_bg_kmb)
            Company.NWFB, Company.CTB -> cardView.setBackgroundResource(R.drawable.eta_card_bg_ctb)
            else -> {
            }
        }
    }

    private fun getEtaMinuteText(eta: TransportEta?): String {
        eta?.eta?.let { etaDate ->
            val minutes = DateUtil.getTimeDiffInMin(
                etaDate,
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

    private fun getEtaTimeText(etaList: List<TransportEta>): String {
        var string = ""

        etaList.forEach {
            string += it.eta.formatString(DateFormat.HH_MM.value) + "    "
        }

        return string.trim()
    }
}