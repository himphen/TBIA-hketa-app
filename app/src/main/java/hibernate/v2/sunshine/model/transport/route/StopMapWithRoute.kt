package hibernate.v2.sunshine.model.transport.route

import hibernate.v2.sunshine.model.transport.eta.EtaType

class StopMapWithRoute(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    val etaType: EtaType,
    val routeNumberList: List<String>
)
