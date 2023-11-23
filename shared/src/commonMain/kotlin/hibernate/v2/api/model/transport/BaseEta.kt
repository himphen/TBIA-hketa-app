package hibernate.v2.api.model.transport

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
enum class Bound(val value: String) : Parcelable {
    @SerialName("I")
    I("I"),

    @SerialName("O")
    O("O"),

    @SerialName("UNKNOWN")
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

@Serializable
@Parcelize
enum class Company(val value: String) : Parcelable {
    @SerialName("KMB")
    KMB("KMB"),

    @SerialName("CTB")
    CTB("CTB"),

    @SerialName("GMB")
    GMB("GMB"),

    @SerialName("MTR")
    MTR("MTR"),

    @SerialName("LRT")
    LRT("LRT"),

    @SerialName("NLB")
    NLB("NLB"),

    @SerialName("UNKNOWN")
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}

@Serializable
enum class GmbRegion(val value: String, val ordering: Int) {
    @SerialName("HKI")
    HKI("HKI", 0),

    @SerialName("KLN")
    KLN("KLN", 1),

    @SerialName("NT")
    NT("NT", 2),

    @SerialName("UNKNOWN")
    UNKNOWN("UNKNOWN", -1);

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: UNKNOWN
    }
}
