package hibernate.v2.domain.mtr

import hibernate.v2.database.mtr.MtrDao

class InitDatabase(
    private val mtrDao: MtrDao
) {
    operator fun invoke() {
        mtrDao.clearRouteList()
        mtrDao.clearStopList()
        mtrDao.clearRouteStopList()
    }
}
