package hibernate.v2.model.transport

import hibernate.v2.model.transport.route.TransportRoute

data class TransportRouteStopList(
    val route: TransportRoute,
    val stopList: MutableList<TransportStop>
) : TransportHashable, Comparable<TransportRouteStopList> {

    override fun compareTo(other: TransportRouteStopList): Int {
        return route.compareTo(other.route)
    }
}
