package hibernate.v2.sunshine.domain.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.eta.EtaDao

class HasEtaInDb(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company
    ) = etaDao.getSingleEta(
        stopId,
        routeId,
        bound,
        serviceType,
        seq,
        company
    ) != null
}
