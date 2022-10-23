package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaDao

class GetSavedLrtEtaList(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke() = etaDao.getAllLrtEtaWithOrdering()
}
