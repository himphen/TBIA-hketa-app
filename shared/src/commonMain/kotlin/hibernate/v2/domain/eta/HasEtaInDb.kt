package hibernate.v2.domain.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.eta.EtaDao

class HasEtaInDb(
    private val etaDao: EtaDao,
) {
    operator fun invoke(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ) = etaDao.hasEtaInDb(
        stopId,
        routeId,
        bound,
        serviceType,
        seq,
        company
    )
}
