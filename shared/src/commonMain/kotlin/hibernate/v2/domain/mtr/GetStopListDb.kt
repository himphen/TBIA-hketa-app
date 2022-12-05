package hibernate.v2.domain.mtr

import hibernate.v2.database.mtr.MtrDao

class GetStopListDb(
    private val mtrDao: MtrDao
) {
    operator fun invoke(): List<Any> =
        mtrDao.getStopList()
}
