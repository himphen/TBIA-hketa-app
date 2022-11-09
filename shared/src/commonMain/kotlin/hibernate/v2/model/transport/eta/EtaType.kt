package hibernate.v2.model.transport.eta

import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource
import hibernate.v2.MR
import hibernate.v2.api.model.transport.Company
import kotlinx.serialization.Serializable

@Serializable
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

    fun name(): StringResource {
        return when (this) {
            KMB -> MR.strings.add_eta_brand_selection_kmb_btn
            NWFB -> MR.strings.add_eta_brand_selection_nwfb_btn
            CTB -> MR.strings.add_eta_brand_selection_ctb_btn
            GMB_HKI -> MR.strings.add_eta_brand_selection_gmb_hki_btn
            GMB_KLN -> MR.strings.add_eta_brand_selection_gmb_kln_btn
            GMB_NT -> MR.strings.add_eta_brand_selection_gmb_nt_btn
            MTR -> MR.strings.add_eta_brand_selection_mtr_btn
            LRT -> MR.strings.add_eta_brand_selection_lrt_btn
            NLB -> MR.strings.add_eta_brand_selection_nlb_btn
        }
    }

    fun color(): ColorResource = when (this) {
        KMB -> MR.colors.brand_color_kmb
        NWFB -> MR.colors.brand_color_nwfb
        GMB_HKI,
        GMB_KLN,
        GMB_NT -> MR.colors.brand_color_gmb
        CTB -> MR.colors.brand_color_ctb
        MTR -> MR.colors.brand_color_mtr
        LRT -> MR.colors.brand_color_lrt
        NLB -> MR.colors.brand_color_nlb
    }
}
