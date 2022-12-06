package hibernate.v2.domain.gmb

import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.database.gmb.GmbDao

class GetRouteListDb(
    private val gmbDao: GmbDao
) {
    operator fun invoke(region: GmbRegion) =
        gmbDao.getRouteList(region)
}
