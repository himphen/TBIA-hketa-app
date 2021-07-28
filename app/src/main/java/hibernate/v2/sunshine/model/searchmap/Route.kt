package hibernate.v2.sunshine.model.searchmap

import hibernate.v2.sunshine.model.transport.TransportRoute

data class Route(
    val route: TransportRoute,
    val seq: Int
)