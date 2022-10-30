package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.database.lrt.LrtDao

class InitDatabase(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke() {
        lrtDao.clearRouteList()
        lrtDao.clearStopList()
        lrtDao.clearRouteStopList()
    }
}
