package hibernate.v2.sunshine.model.transport

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company

interface TransportHashable {
    fun routeHashId(
        company: Company,
        routeId: String,
        bound: Bound,
        serviceType: String
    ) = company.value + routeId + bound.value + serviceType

    fun stopHashId(
        routeId: String,
        bound: Bound,
        serviceType: String,
        stopId: String,
        seq: Int
    ) = routeId + bound.value + serviceType + stopId + seq
}
