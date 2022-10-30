package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.database.mtr.MtrDao

class GetRouteListDb(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke() =
        mtrDao.getRouteList()

    suspend operator fun invoke(enabled: Boolean) =
        mtrDao.getEnabledRouteList()
}
