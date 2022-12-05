package hibernate.v2.domain.nlb

import hibernate.v2.database.nlb.NlbDao

class GetRouteListDb(
    private val nlbDao: NlbDao
) {
    operator fun invoke() =
        nlbDao.getRouteList()
}
