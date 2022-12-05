package hibernate.v2.domain.gmb

import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.geohash.GeoHash

class GetStopListDb(
    private val gmbDao: GmbDao
) {
    operator fun invoke(list: List<GeoHash>) =
        gmbDao.getStopList(list.map { it.toString() })
}
