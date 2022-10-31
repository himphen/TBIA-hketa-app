package hibernate.v2.sunshine.domain.eta

import hibernate.v2.database.eta.EtaDao

class ClearAllEta(
    private val etaDao: EtaDao,
) {
    operator fun invoke() {
        etaDao.clearAllEta()
    }
}
