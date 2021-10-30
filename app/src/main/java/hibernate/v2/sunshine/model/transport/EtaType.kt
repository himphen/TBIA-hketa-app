package hibernate.v2.sunshine.model.transport

import android.content.Context
import androidx.annotation.ColorInt
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R

enum class EtaType {
    KMB,
    NWFB,
    CTB,
    GMB_HKI,
    GMB_KLN,
    GMB_NT,
    MTR,
    LRT,
    NLB;

    fun company(): Company = when (this) {
        KMB -> Company.KMB
        NWFB -> Company.NWFB
        CTB -> Company.CTB
        GMB_HKI, GMB_KLN, GMB_NT -> Company.GMB
        MTR -> Company.MTR
        LRT -> Company.LRT
        NLB -> Company.NLB
    }

    fun name(context: Context): String = when (this) {
        KMB -> context.getString(R.string.dialog_add_eta_brand_selection_kmb_btn)
        NWFB -> context.getString(R.string.dialog_add_eta_brand_selection_nwfb_btn)
        CTB -> context.getString(R.string.dialog_add_eta_brand_selection_ctb_btn)
        GMB_HKI -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_hki_btn)
        GMB_KLN -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_kln_btn)
        GMB_NT -> context.getString(R.string.dialog_add_eta_brand_selection_gmb_nt_btn)
        MTR -> context.getString(R.string.dialog_add_eta_brand_selection_mtr_btn)
        LRT -> context.getString(R.string.dialog_add_eta_brand_selection_lrt_btn)
        NLB -> context.getString(R.string.dialog_add_eta_brand_selection_nlb_btn)
    }

    @ColorInt
    fun color(context: Context): Int = when (this) {
        KMB -> context.getColor(R.color.brand_color_kmb)
        NWFB -> context.getColor(R.color.brand_color_nwfb)
        GMB_HKI, GMB_KLN, GMB_NT -> context.getColor(R.color.brand_color_gmb)
        CTB -> context.getColor(R.color.brand_color_ctb)
        MTR -> context.getColor(R.color.brand_color_mtr)
        LRT -> context.getColor(R.color.brand_color_lrt)
        NLB -> context.getColor(R.color.brand_color_nlb)
    }
}