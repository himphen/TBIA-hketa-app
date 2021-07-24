package hibernate.v2.sunshine.model.transport

import android.content.Context
import hibernate.v2.sunshine.R

enum class EtaType {
    KMB,
    NWFB_CTB,
    GMB;

    fun etaTypeName(context: Context) = when (this) {
        KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
        NWFB_CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
        GMB -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
    }

    fun etaTypeColors(context: Context): IntArray = when (this) {
        KMB -> intArrayOf(context.getColor(R.color.brand_color_kmb))
        NWFB_CTB -> intArrayOf(
            context.getColor(R.color.brand_color_nwfb),
//                context.getColor(R.color.brand_color_ctb)
        )
        GMB -> intArrayOf(context.getColor(R.color.brand_color_gmb))
    }
}