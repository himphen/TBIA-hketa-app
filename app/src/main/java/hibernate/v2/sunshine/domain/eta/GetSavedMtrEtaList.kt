package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaDao

class GetSavedMtrEtaList(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke() = etaDao.getAllMtrEtaWithOrdering()
}
