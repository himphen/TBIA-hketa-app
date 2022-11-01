package hibernate.v2.api.model.transport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusEta(
    @SerialName("co")
    val company: String? = null,
    @SerialName("dest_en")
    val destEn: String? = null,
    @SerialName("dest_sc")
    val destSc: String? = null,
    @SerialName("dest_tc")
    val destTc: String? = null,
    @SerialName("dir")
    val bound: Bound? = null,
    @SerialName("eta")
    private val eta: String? = null,
    @SerialName("rmk_en")
    val rmkEn: String? = null,
    @SerialName("rmk_sc")
    val rmkSc: String? = null,
    @SerialName("rmk_tc")
    val rmkTc: String? = null,
    @SerialName("route")
    val route: String? = null,
    @SerialName("seq")
    val seq: Int? = null,
    @SerialName("service_type")
    val serviceType: Int? = null
) {
    fun getEta(): String? {
        if (eta == "") return null

        return eta
    }
}
