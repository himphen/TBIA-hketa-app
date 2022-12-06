package hibernate.v2.model.dataholder

import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.route.TransportRoute

class AddEtaRowItem(
    val headerTitle: String,
    val route: TransportRoute,
    val filteredList: List<TransportStop>
)
