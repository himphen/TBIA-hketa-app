package hibernate.v2.database.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable

data class SavedEtaEntity(
    val id: Long? = null,
    val company: Company,
    val stopId: String,
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val seq: Int,
) : TransportHashable {

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    fun stopHashId() = stopHashId(routeId, bound, serviceType, stopId, seq)
}
