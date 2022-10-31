package hibernate.v2.model.transport

data class RouteDetailsStop(
    val transportStop: TransportStop,
    var savedEtaId: Int?
)
