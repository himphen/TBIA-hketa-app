package hibernate.v2.model.transport.eta

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
}
