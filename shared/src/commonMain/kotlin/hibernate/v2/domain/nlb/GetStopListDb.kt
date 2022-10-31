package hibernate.v2.domain.nlb

import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.geohash.GeoHash

class GetStopListDb(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        nlbDao.getStopList(list.map { it.toString() })
}
