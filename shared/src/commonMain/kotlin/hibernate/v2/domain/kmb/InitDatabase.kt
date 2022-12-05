package hibernate.v2.domain.kmb

import hibernate.v2.database.kmb.KmbDao

class InitDatabase(
    private val kmbDao: KmbDao
) {
    operator fun invoke() {
        kmbDao.clearRouteList()
        kmbDao.clearStopList()
        kmbDao.clearRouteStopList()
    }
}
