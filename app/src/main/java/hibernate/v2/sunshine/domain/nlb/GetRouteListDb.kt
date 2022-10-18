package hibernate.v2.sunshine.domain.nlb

import hibernate.v2.sunshine.db.nlb.NlbDao

class GetRouteListDb(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke() =
        nlbDao.getRouteList()
}
