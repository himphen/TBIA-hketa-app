package hibernate.v2.api.model.transport.gmb

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.GmbRegion

@Keep
data class GmbRouteStop(
    val bound: Bound = Bound.O,
    @SerializedName("region")
    var region: GmbRegion = GmbRegion.UNKNOWN,
    @SerializedName("route_id")
    var routeId: Long = 0,
    var seq: Int = 0,
    @SerializedName("stop")
    var stopId: Long = 0,
    @SerializedName("service_type")
    var serviceType: Long = 0,
)
