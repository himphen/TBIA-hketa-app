package hibernate.v2.api.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("dest_en")
    val destEn: String,
    @SerializedName("dest_sc")
    val destSc: String,
    @SerializedName("dest_tc")
    val destTc: String,
    @SerializedName("orig_en")
    val origEn: String,
    @SerializedName("orig_sc")
    val origSc: String,
    @SerializedName("orig_tc")
    val origTc: String,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("service_type")
    val serviceType: String,

    var routeParsed: Boolean = false,
    var routePrefix: String? = null,
    var routeNumber: Int? = null,
    var routeSuffix: String? = null
) : Parcelable, Comparable<Route> {
    override fun compareTo(other: Route): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val routePrefixCompare = routePrefix!!.compareTo(other.routePrefix!!)
        if (routePrefixCompare != 0) return routePrefixCompare

        val routeCompare = routeNumber!!.compareTo(other.routeNumber!!)
        if (routeCompare != 0) return routeCompare

        val routeSuffixCompare = routeSuffix!!.compareTo(other.routeSuffix!!)
        if (routeSuffixCompare != 0) return routeSuffixCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }

    fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }

    fun isSpecialRoute(): Boolean = serviceType != "1"
    private fun parseRouteNumber() {
        if (!routeParsed) {
            Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeId)?.let { match ->
                routePrefix = match.destructured.component1()
                routeNumber = match.destructured.component2().toInt()
                routeSuffix = match.destructured.component3()
            }
            routeParsed = true
        }
    }
}

enum class Bound(val value: String) {
    @Keep
    @SerializedName("I")
    INBOUND("inbound"),

    @Keep
    @SerializedName("O")
    OUTBOUND("outbound");

    companion object {
        fun from(type: String?) = values().find { it.value == type } ?: INBOUND
    }
}