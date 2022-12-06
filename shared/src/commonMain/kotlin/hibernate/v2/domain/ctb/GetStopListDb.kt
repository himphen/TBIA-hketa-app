package hibernate.v2.domain.ctb

import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.geohash.GeoHash

class GetStopListDb(
    private val ctbDao: CtbDao
) {
    operator fun invoke(list: List<GeoHash>) =
        ctbDao.getStopList(list.map { it.toString() })
}
