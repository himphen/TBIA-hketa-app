package hibernate.v2.domain.eta

import hibernate.v2.database.eta.EtaDao

class GetSavedNCEtaList(
    private val etaDao: EtaDao,
) {
    operator fun invoke() = etaDao.getAllCtbEtaWithOrdering()
}
