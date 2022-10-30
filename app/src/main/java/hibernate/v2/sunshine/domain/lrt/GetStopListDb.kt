package hibernate.v2.sunshine.domain.lrt

import hibernate.v2.database.lrt.LrtDao

class GetStopListDb(
    private val lrtDao: LrtDao
) {
    suspend operator fun invoke(): List<Any> =
        lrtDao.getStopList()
}
