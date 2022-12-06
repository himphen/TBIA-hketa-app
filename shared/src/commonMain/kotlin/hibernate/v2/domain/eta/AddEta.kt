package hibernate.v2.domain.eta

import hibernate.v2.database.eta.EtaDao
import hibernate.v2.database.eta.SavedEtaEntity

class AddEta(
    private val etaDao: EtaDao,
) {
    operator fun invoke(entity: SavedEtaEntity) =
        etaDao.addEta(entity)
}
