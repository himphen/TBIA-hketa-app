package hibernate.v2.api.model.transport

import androidx.annotation.Keep

enum class Bound(val value: String) {
    @Keep
    I("inbound"),

    @Keep
    O("outbound"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

enum class Company(val value: String) {
    @Keep
    KMB("kmb"),

    @Keep
    NWFB("nwfb"),

    @Keep
    CTB("ctb"),

    @Keep
    GMB("gmb"),

    @Keep
    MTR("mtr"),

    @Keep
    LRT("lrt"),

    @Keep
    NLB("nlb"),

    @Keep
    UNKNOWN("unknown");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

enum class GmbRegion(val value: String, val ordering: Int) {
    @Keep
    HKI("HKI", 0),

    @Keep
    KLN("KLN", 1),

    @Keep
    NT("NT", 2),

    @Keep
    UNKNOWN("unknown", -1);

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}
