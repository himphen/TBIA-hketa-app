package hibernate.v2.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Route
import kotlinx.parcelize.Parcelize

@Parcelize
data class EtaRequest(
    @SerializedName("stopId") val stopId: String,
    @SerializedName("route") val route: RouteRequest,
) : Parcelable