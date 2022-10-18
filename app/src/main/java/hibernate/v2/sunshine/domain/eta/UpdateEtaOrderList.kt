package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity

class UpdateEtaOrderList(
    private val etaOrderDb: EtaOrderDao,
) {
    suspend operator fun invoke(entityList: List<EtaOrderEntity>) {
        etaOrderDb.clearAll()
        etaOrderDb.add(entityList)
    }
}
