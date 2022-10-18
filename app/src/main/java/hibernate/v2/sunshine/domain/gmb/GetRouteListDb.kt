package hibernate.v2.sunshine.domain.gmb

import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.sunshine.db.gmb.GmbDao

class GetRouteListDb(
    private val gmbDao: GmbDao
) {
    suspend operator fun invoke(region: GmbRegion) =
        gmbDao.getRouteList(region.value)
}
