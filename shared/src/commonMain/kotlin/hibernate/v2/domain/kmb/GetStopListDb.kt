package hibernate.v2.domain.kmb

import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.geohash.GeoHash

class GetStopListDb(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        kmbDao.getStopList(list.map { it.toString() })
}
