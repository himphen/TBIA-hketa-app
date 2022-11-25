package hibernate.v2.domain.mtr

import hibernate.v2.database.mtr.MtrDao

class GetRouteListDb(
    private val mtrDao: MtrDao
) {
    operator fun invoke() =
        mtrDao.getRouteList()

    @Suppress("UNUSED_PARAMETER")
    operator fun invoke(enabled: Boolean) =
        mtrDao.getEnabledRouteList()
}
