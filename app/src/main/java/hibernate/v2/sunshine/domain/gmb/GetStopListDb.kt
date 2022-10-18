package hibernate.v2.sunshine.domain.gmb

import com.fonfon.kgeohash.GeoHash
import hibernate.v2.sunshine.db.gmb.GmbDao

class GetStopListDb(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        gmbDao.getStopList(list.map { it.toString() })
}
