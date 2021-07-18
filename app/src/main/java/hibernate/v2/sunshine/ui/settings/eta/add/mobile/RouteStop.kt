package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.model.transport.TransportStop

class RouteStop(
    val route: TransportRoute,
    val stopList: List<TransportStop>
)