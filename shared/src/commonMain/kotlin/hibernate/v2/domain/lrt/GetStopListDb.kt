package hibernate.v2.domain.lrt

import hibernate.v2.database.lrt.LrtDao

class GetStopListDb(
    private val lrtDao: LrtDao
) {
    operator fun invoke(): List<Any> =
        lrtDao.getStopList()
}
