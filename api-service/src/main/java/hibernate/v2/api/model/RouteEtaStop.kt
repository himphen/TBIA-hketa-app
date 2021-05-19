package hibernate.v2.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteEtaStop(
    @SerializedName("stop")
    val stop: Stop,
    @SerializedName("etaList")
    var etaList: List<Eta>,
    @SerializedName("route")
    val route: Route
) : Parcelable