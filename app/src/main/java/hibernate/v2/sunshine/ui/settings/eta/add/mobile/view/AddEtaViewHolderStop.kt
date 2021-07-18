package hibernate.v2.sunshine.ui.settings.eta.add.mobile.view

import hibernate.v2.sunshine.databinding.ItemAddEtaStopBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaAdapter

class AddEtaViewHolderStop(
    override val viewBinding: ItemAddEtaStopBinding,
    private val listener: AddEtaAdapter.ItemListener
) : BaseAddEtaViewHolder<ItemAddEtaStopBinding>(viewBinding) {

    fun onBind(card: Card.RouteStopAddCard) {
        viewBinding.stopNameTv.text = card.stop.nameTc

        viewBinding.root.tag = card
        viewBinding.root.setOnClickListener { listener.onStopSelected(card) }
    }
}