package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.SavedEtaEntity

class AddEta(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke(entity: SavedEtaEntity) = etaDao.add(entity)
}
