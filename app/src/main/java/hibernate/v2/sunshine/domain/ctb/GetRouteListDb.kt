package hibernate.v2.sunshine.domain.ctb

import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.db.ctb.CtbDao

class GetRouteListDb(
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke(company: Company) =
        ctbDao.getRouteList(company.value)
}