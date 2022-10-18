package hibernate.v2.sunshine.domain.eta

import hibernate.v2.sunshine.db.eta.EtaOrderDao

class GetEtaOrderList(
    private val etaOrderDb: EtaOrderDao,
) {
    suspend operator fun invoke() = etaOrderDb.get()
}
