package hibernate.v2.domain.mtr

import hibernate.v2.database.mtr.MtrDao

class InitDatabase(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke() {
        mtrDao.clearRouteList()
        mtrDao.clearStopList()
        mtrDao.clearRouteStopList()
    }
}
