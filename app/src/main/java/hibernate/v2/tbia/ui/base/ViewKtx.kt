package hibernate.v2.tbia.ui.base

import android.content.Context
import hibernate.v2.model.transport.card.EtaCardViewType
import hibernate.v2.tbia.R

fun EtaCardViewType.getTitle(context: Context): String {
    return when (this) {
        EtaCardViewType.Standard -> context.getString(R.string.dialog_eta_layout_standard_btn)
        EtaCardViewType.Classic -> context.getString(R.string.dialog_eta_layout_classic_btn)
        EtaCardViewType.Compact -> context.getString(R.string.dialog_eta_layout_compact_btn)
    }
}
