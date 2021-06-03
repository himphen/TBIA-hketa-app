package hibernate.v2.sunshine.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Eta
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
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