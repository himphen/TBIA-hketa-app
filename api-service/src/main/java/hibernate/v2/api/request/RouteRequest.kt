package hibernate.v2.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteRequest(
    @SerializedName("bound") val bound: Bound,
    @SerializedName("route") val routeId: String,
) : Parcelable {
    enum class Bound(val value: String, val param: String) {
        INBOUND("I", "inbound"),
        OUTBOUND("O", "outbound"),
    }
}