package hibernate.v2.sunshine.ui.eta.home.leanback.view

import android.content.Context
import android.view.LayoutInflater
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ContentEtaClassicBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.EtaTimeAdapter
import hibernate.v2.sunshine.ui.eta.view.EtaRouteView
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EtaCardViewClassic(context: Context) :
    BaseEtaCardView<ContentEtaClassicBinding>(context),
    EtaRouteView {

    override var viewBinding =
        ContentEtaClassicBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onBind(card: Card.EtaCard) {
        viewBinding.apply {
            applyRouteNumberContainer(card, routeNumberContainer)

            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)

            (etaMinuteLl.etaTimeRv.adapter as EtaTimeAdapter).apply {
                setData(card.etaList)
            }

            card.etaList.getOrNull(0)?.getEtaMinuteText("-")?.let {
                etaMinuteLl.etaMinuteTv.text = it.second

                if (it.first) {
                    etaMinuteLl.etaMinuteUnitTv.visible()
                } else {
                    etaMinuteLl.etaMinuteUnitTv.gone()
                }
            } ?: run {
                etaMinuteLl.etaMinuteTv.text =
                    context.getString(R.string.demo_card_eta_minute_na)
                etaMinuteLl.etaMinuteUnitTv.gone()
            }
        }
    }
}