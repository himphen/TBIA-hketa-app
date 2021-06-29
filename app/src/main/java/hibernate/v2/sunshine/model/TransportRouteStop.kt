package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Bound
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportRouteStop(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: String,
    val stopId: String,
) : Parcelable, RouteHashable, StopHashable {
    override fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }

    override fun stopHashId(): String {
        return routeId + bound.value + serviceType + stopId + seq
    }
}