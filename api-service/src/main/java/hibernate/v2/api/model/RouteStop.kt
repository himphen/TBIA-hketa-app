package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteStop(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("seq")
    val seq: String,
    @SerializedName("service_type")
    val serviceType: String,
    @SerializedName("stop")
    val stopId: String,
) : Parcelable {
    fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }
}