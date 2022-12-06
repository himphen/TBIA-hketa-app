package hibernate.v2.domain.nlb

import hibernate.v2.database.nlb.NlbDao

class InitDatabase(
    private val nlbDao: NlbDao
) {
    operator fun invoke() {
        nlbDao.clearRouteList()
        nlbDao.clearStopList()
        nlbDao.clearRouteStopList()
    }
}
