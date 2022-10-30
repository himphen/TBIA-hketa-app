package hibernate.v2.sunshine.domain.eta

import hibernate.v2.database.eta.EtaOrderDao
import hibernate.v2.database.eta.SavedEtaOrderEntity

class UpdateEtaOrderList(
    private val etaOrderDb: EtaOrderDao,
) {
    suspend operator fun invoke(entityList: List<SavedEtaOrderEntity>) {
        etaOrderDb.clearAll()
        etaOrderDb.add(entityList)
    }
}
