package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.sunshine.db.lrt.LrtDao

class GetRouteListDb(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke() =
        lrtDao.getRouteList()

    suspend operator fun invoke(enabled: Boolean) =
        lrtDao.getRouteList(enabled)
}
