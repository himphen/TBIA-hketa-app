package hibernate.v2.sunshine.model

data class TransportRouteStopList(
    val route: TransportRoute,
    val stopList: MutableList<TransportStop>
) : RouteHashable, Comparable<TransportRouteStopList> {
    override fun routeHashId(): String {
        return route.routeHashId()
    }

    override fun compareTo(other: TransportRouteStopList): Int {
        return route.compareTo(other.route)
    }
}