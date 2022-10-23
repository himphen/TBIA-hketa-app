package hibernate.v2.sunshine.domain.mtr

import hibernate.v2.sunshine.db.mtr.MtrDao

class InitDatabase(
    private val mtrDao: MtrDao
) {
    suspend operator fun invoke() {
        mtrDao.clearRouteList()
        mtrDao.clearStopList()
        mtrDao.clearRouteStopList()
    }
}
