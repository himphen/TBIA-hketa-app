package hibernate.v2.domain.eta

import hibernate.v2.database.eta.EtaDao
import hibernate.v2.database.eta.SavedEtaEntity

class ClearEta(
    private val etaDao: EtaDao,
) {
    operator fun invoke(entityId: Int) {
        etaDao.clearEta(entityId)
    }

    operator fun invoke(entity: SavedEtaEntity) {
        etaDao.clearEta(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            entity.seq
        )
    }
}
