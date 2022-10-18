package hibernate.v2.sunshine.domain.ctb

import hibernate.v2.sunshine.db.ctb.CtbDao

class InitDatabase(
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke() {
        ctbDao.clearRouteList()
        ctbDao.clearStopList()
        ctbDao.clearRouteStopList()
    }
}
