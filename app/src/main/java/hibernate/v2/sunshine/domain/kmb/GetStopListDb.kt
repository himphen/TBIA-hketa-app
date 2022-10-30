package hibernate.v2.sunshine.domain.kmb

import com.fonfon.kgeohash.GeoHash
import hibernate.v2.database.kmb.KmbDao

class GetStopListDb(
    private val kmbDao: KmbDao
) {
    suspend operator fun invoke(list: List<GeoHash>) =
        kmbDao.getStopList(list.map { it.toString() })
}
