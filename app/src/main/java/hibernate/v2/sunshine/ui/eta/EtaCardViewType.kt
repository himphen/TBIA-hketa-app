package hibernate.v2.sunshine.ui.eta

import android.content.Context
import hibernate.v2.sunshine.R

enum class EtaCardViewType(val value: Int) {
    Standard(0), Classic(1), Compact(2);

    fun getTitle(context: Context): String {
        return when (this) {
            Standard -> context.getString(R.string.dialog_eta_layout_standard_btn)
            Classic -> context.getString(R.string.dialog_eta_layout_classic_btn)
            Compact -> context.getString(R.string.dialog_eta_layout_compact_btn)
        }
    }

    companion object {
        fun from(value: Int?) = values().find { it.value == value } ?: Standard
    }
}