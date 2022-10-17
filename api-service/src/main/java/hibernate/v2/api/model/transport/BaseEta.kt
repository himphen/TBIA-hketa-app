package hibernate.v2.api.model.transport

import kotlinx.serialization.Serializable

@Serializable
enum class Bound(val value: String) {
    I("inbound"),

    O("outbound"),

    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

@Serializable
enum class Company(val value: String) {
    KMB("kmb"),

    NWFB("nwfb"),

    CTB("ctb"),

    GMB("gmb"),

    MTR("mtr"),

    LRT("lrt"),

    NLB("nlb"),

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
