package hibernate.v2.sunshine.model

import hibernate.v2.sunshine.model.transport.TransportRoute

class RouteForRowAdapter(
    val headerTitle: String,
    val route: TransportRoute,
    val filteredList: List<Card.RouteStopAddCard>
)