package hibernate.v2.sunshine.domain.eta

import hibernate.v2.database.eta.EtaDao

class GetSavedGmbEtaList(
    private val etaDao: EtaDao,
) {
    suspend operator fun invoke() = etaDao.getAllGmbEtaWithOrdering()
}
