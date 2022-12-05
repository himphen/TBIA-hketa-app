package hibernate.v2.domain.gmb

import hibernate.v2.database.gmb.GmbDao

class InitDatabase(
    private val gmbDao: GmbDao
) {
    operator fun invoke() {
        gmbDao.clearRouteList()
        gmbDao.clearStopList()
        gmbDao.clearRouteStopList()
    }
}
