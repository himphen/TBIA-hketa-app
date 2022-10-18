package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.sunshine.db.gmb.GmbDao

class InitDatabase(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke() {
        gmbDao.clearRouteList()
        gmbDao.clearStopList()
        gmbDao.clearRouteStopList()
    }
}
