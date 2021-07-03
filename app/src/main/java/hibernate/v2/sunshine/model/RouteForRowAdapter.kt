package hibernate.v2.sunshine.model

class RouteForRowAdapter(
    val headerTitle: String,
    val route: TransportRoute,
    val filteredList: List<Card.RouteStopAddCard>
)