package hibernate.v2.sunshine.domain.ctb

import com.fonfon.kgeohash.GeoHash
import hibernate.v2.database.ctb.CtbDao

class GetStopListDb(
    private val ctbDao: CtbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        ctbDao.getStopList(list.map { it.toString() })
}
