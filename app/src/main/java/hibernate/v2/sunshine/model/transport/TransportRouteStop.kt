package hibernate.v2.sunshine.model.transport

import hibernate.v2.api.model.Bound

data class TransportRouteStop(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: String,
    val stopId: String,
) : RouteHashable, StopHashable {
    override fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }

    override fun stopHashId(): String {
        return routeId + bound.value + serviceType + stopId + seq
    }
}