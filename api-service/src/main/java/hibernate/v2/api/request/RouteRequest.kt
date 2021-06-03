package hibernate.v2.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteRequest(
    @SerializedName("bound") val bound: String,
    @SerializedName("route") val routeId: String,
    @SerializedName("serviceType") val serviceType: String,
) : Parcelable {

    fun hashId(): String {
        return routeId + bound + serviceType
    }
}