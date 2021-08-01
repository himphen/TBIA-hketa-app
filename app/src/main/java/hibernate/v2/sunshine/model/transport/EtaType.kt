package hibernate.v2.sunshine.model.transport

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R

enum class EtaType {
    KMB,
    NWFB,
    CTB,
    GMB;

    fun company() = when (this) {
        KMB -> Company.KMB
        NWFB -> Company.NWFB
        CTB -> Company.CTB
        GMB -> Company.GMB
    }

    fun name(context: Context) = when (this) {
        KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
        NWFB -> context.getString(R.string.dialog_add_eta_brand_selection_nwfb_btn)
        CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
        GMB -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
    }

    @ColorInt
    fun color(context: Context): Int = when (this) {
        KMB -> context.getColor(R.color.brand_color_kmb)
        NWFB -> context.getColor(R.color.brand_color_nwfb)
        GMB -> context.getColor(R.color.brand_color_gmb)
        CTB -> context.getColor(R.color.brand_color_ctb)
    }
}

object EtaType1 {

    const val KMB = 1
    const val NWFB = 2
    const val CTB = 3
    const val GMB = 4

    @IntDef(KMB, NWFB, CTB, GMB)
    @Retention(AnnotationRetention.SOURCE)
    annotation class EtaTypeInt

    fun company(@EtaTypeInt value: Int) = when (value) {
        KMB -> Company.KMB
        NWFB -> Company.NWFB
        CTB -> Company.CTB
        GMB -> Company.GMB
        else -> Company.KMB
    }

    fun name(context: Context, @EtaTypeInt value: Int) = when (value) {
        KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
        NWFB -> context.getString(R.string.dialog_add_eta_brand_selection_nwfb_btn)
        CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
        GMB -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_btn)
        else -> ""
    }

    @ColorInt
    fun color(context: Context, @EtaTypeInt value: Int): Int = when (value) {
        KMB -> context.getColor(R.color.brand_color_kmb)
        NWFB -> context.getColor(R.color.brand_color_nwfb)
        GMB -> context.getColor(R.color.brand_color_gmb)
        CTB -> context.getColor(R.color.brand_color_ctb)
        else -> 0
    }
}