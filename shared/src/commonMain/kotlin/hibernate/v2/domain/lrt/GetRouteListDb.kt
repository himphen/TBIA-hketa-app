package hibernate.v2.domain.lrt

import hibernate.v2.database.lrt.LrtDao

class GetRouteListDb(
    private val lrtDao: LrtDao
) {
    operator fun invoke() =
        lrtDao.getRouteList()

    @Suppress("UNUSED_PARAMETER")
    operator fun invoke(enabled: Boolean) =
        lrtDao.getEnabledRouteList()
}
