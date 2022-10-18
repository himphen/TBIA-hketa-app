package hibernate.v2.sunshine.domain.kmb

import hibernate.v2.sunshine.db.kmb.KmbDao

class GetRouteListDb(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke() =
        kmbDao.getRouteList()
}