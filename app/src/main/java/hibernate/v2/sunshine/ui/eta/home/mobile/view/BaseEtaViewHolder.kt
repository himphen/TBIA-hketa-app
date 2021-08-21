package hibernate.v2.sunshine.ui.eta.home.mobile.view

import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseViewHolder

abstract class BaseEtaViewHolder<T : ViewBinding>(viewBinding: T) :
    BaseViewHolder<T>(viewBinding) {

    abstract fun onBind(card: Card.EtaCard)
}