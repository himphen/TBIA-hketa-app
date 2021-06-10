package hibernate.v2.sunshine.ui.eta

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardEtaBinding

class EtaCardView(context: Context) : BaseCardView(
    context,
    null
) {
    var viewBinding = CardEtaBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        isFocusable = true
    }
}