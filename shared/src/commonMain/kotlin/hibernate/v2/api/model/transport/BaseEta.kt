package hibernate.v2.api.model.transport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Bound(val value: String) {
    @SerialName("inbound")
    I("inbound"),

    @SerialName("outbound")
    O("outbound"),

    @SerialName("unknown")
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

@Serializable
enum class Company(val value: String) {
    @SerialName("kmb")
    KMB("kmb"),

    @SerialName("nwfb")
    NWFB("nwfb"),

    @SerialName("ctb")
    CTB("ctb"),

    @SerialName("gmb")
    GMB("gmb"),

    @SerialName("mtr")
    MTR("mtr"),

    @SerialName("lrt")
    LRT("lrt"),

    @SerialName("nlb")
    NLB("nlb"),

    @SerialName("unknown")
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

enum class GmbRegion(val value: String, val ordering: Int) {
    HKI("HKI", 0),

    KLN("KLN", 1),

    NT("NT", 2),

    UNKNOWN("unknown", -1);

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}
