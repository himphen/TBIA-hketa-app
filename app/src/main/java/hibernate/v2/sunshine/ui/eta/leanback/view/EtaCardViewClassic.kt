package hibernate.v2.sunshine.ui.eta.leanback.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaClassicBinding
import hibernate.v2.sunshine.model.Card

class EtaCardViewClassic(context: Context) : BaseEtaCardView<ContentEtaClassicBinding>(context) {
    override var viewBinding =
        ContentEtaClassicBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
        val color = when (card.route.company) {
            Company.KMB -> R.color.brand_color_kmb
            Company.NWFB -> R.color.brand_color_nwfb
            Company.CTB -> R.color.brand_color_ctb
            Company.GMB -> R.color.brand_color_gmb
            Company.MTR -> R.color.brand_color_mtr
            Company.LRT -> R.color.brand_color_lrt
            Company.UNKNOWN -> R.color.eta_card_bg_selected
        }

        viewBinding.routeCompanyColor.setBackgroundResource(color)
        viewBinding.routeNumberTv.text = card.route.routeNo
        viewBinding.stopNameTv.text = card.stop.getName(context)
        viewBinding.routeDirectionTv.text = card.route.getDestDirectionText(context)
        viewBinding.etaMinuteTv.text = card.etaList.getOrNull(0)?.getEtaMinuteText("")?.second
        viewBinding.etaTimeTv.text = getEtaTimeText(card.etaList)

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