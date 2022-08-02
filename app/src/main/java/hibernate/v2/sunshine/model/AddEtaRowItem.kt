package hibernate.v2.sunshine.model

import hibernate.v2.sunshine.model.transport.route.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

class AddEtaRowItem(
    val headerTitle: String,
    val route: TransportRoute,
    val filteredList: List<TransportStop>
)
