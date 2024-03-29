package hibernate.v2.domain.eta

import hibernate.v2.database.eta.EtaOrderDao

class GetEtaOrderList(
    private val etaOrderDb: EtaOrderDao,
) {
    operator fun invoke() = etaOrderDb.getEtaOrder()
}
