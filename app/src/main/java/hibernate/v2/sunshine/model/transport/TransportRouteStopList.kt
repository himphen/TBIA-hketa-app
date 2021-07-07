package hibernate.v2.sunshine.model.transport

data class TransportRouteStopList(
    val route: TransportRoute,
    val stopList: MutableList<TransportStop>
) : TransportHashable, Comparable<TransportRouteStopList> {

    override fun compareTo(other: TransportRouteStopList): Int {
        return route.compareTo(other.route)
    }
}