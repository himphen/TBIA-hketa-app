package hibernate.v2.domain.kmb

import hibernate.v2.database.kmb.KmbDao

class GetRouteListDb(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke() =
        kmbDao.getRouteList()
}
