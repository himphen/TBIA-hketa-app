package hibernate.v2.sunshine.domain.nlb

import com.fonfon.kgeohash.GeoHash
import hibernate.v2.database.nlb.NlbDao

class GetStopListDb(
    private val nlbDao: NlbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        nlbDao.getStopList(list.map { it.toString() })
}
