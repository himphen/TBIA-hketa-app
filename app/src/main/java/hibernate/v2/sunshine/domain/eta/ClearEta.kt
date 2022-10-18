package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.SavedEtaEntity

class ClearEta(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke(entityId: Long) {
        etaDao.clear(entityId)
    }

    suspend operator fun invoke(entity: SavedEtaEntity) {
        etaDao.clear(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            ""
        )
    }
}
