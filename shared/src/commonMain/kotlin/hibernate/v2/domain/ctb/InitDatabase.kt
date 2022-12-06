package hibernate.v2.domain.ctb

import hibernate.v2.database.ctb.CtbDao

class InitDatabase(
    private val ctbDao: CtbDao
) {
    operator fun invoke() {
        ctbDao.clearRouteList()
        ctbDao.clearStopList()
        ctbDao.clearRouteStopList()
    }
}
