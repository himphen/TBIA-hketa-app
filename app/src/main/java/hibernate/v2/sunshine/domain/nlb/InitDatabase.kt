package hibernate.v2.sunshine.domain.nlb

import hibernate.v2.database.nlb.NlbDao

class InitDatabase(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke() {
        nlbDao.clearRouteList()
        nlbDao.clearStopList()
        nlbDao.clearRouteStopList()
    }
}
