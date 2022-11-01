package hibernate.v2.domain.lrt

import hibernate.v2.database.lrt.LrtDao

class GetRouteListDb(
    private val lrtDao: LrtDao
) {
    operator fun invoke() =
        lrtDao.getRouteList()

    operator fun invoke(enabled: Boolean) =
        lrtDao.getRouteList()
}
