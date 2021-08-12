package hibernate.v2.sunshine.model

import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

class RouteForRowAdapter(
    val headerTitle: String,
    val route: TransportRoute,
    val filteredList: List<TransportStop>
)