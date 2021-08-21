package hibernate.v2.sunshine.ui.eta.edit.leanback

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.LbCardSettingsEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.view.EtaRouteView
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible

class EditEtaCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
), EtaRouteView {
    var viewBinding = LbCardSettingsEtaBinding.inflate(LayoutInflater.from(context), this, true)

    fun onBindItemView(card: Card.SettingsEtaItemCard) {
        viewBinding.apply {
            applyRouteNumberContainer(card, routeNumberContainer)
            stopNameTv.text = card.stop.getName(context)
            routeDirectionTv.text = card.route.getDestDirectionText(context)
            routeDirectionTv.visibility = VISIBLE
        }
    }

    fun onBindAddView() {
        viewBinding.apply {
            routeNumberContainer.routeCompanyColor.background = null
            routeNumberContainer.routeBusNumberTv.text = "+"
            routeNumberContainer.routeBusNumberLl.visible()
            routeNumberContainer.routeMTRNumberLl.gone()
            routeNumberContainer.routeLRTNumberLl.gone()
            stopNameTv.text = "按此加入"
            routeDirectionTv.visibility = GONE
        }
    }

    init {
        isFocusable = true
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundResource(R.color.settings_card_background_focussed)
            } else {
                setBackgroundResource(R.color.settings_card_background)
            }
        }
        setBackgroundResource(R.color.settings_card_background)
    }
}