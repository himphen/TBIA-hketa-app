package hibernate.v2.sunshine.domain.eta

import hibernate.v2.database.eta.EtaDao
import hibernate.v2.database.eta.SavedEtaEntity

class ClearEta(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke(entityId: Long) {
        etaDao.clearEta(entityId)
    }

    suspend operator fun invoke(entity: SavedEtaEntity) {
        etaDao.clearEta(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            entity.seq
        )
    }
}
