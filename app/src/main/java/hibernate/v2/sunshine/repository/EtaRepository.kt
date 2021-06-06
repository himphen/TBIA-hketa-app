package hibernate.v2.sunshine.repository

import android.content.Context
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.EtaDatabase
import hibernate.v2.sunshine.db.eta.EtaEntity

class EtaRepository private constructor(database: EtaDatabase) {

    // Database related fields/methods:
    private val dao = database.etaDao()

    /**
     * Returns all recorded tracking data from database.
     */
    suspend fun getData(): List<EtaEntity> = dao.get()

    /**
     * Returns specific tracking data in database.
     */
    fun getData(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    ): List<EtaEntity> = dao.get(
        stopId,
        routeId,
        bound,
        serviceType,
        seq
    )

    /**
     * Adds tracking data to the database.
     */
    fun addData(entity: EtaEntity) {
        dao.add(entity)
    }

    /**
     * Adds list of tracking data to the database.
     */
    fun clearData(entity: EtaEntity) {
        dao.clear(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            entity.seq
        )
    }

    /**
     * Clear list of tracking data to the database.
     */
    fun clearAllData() {
        dao.clearAll()
    }

    companion object {
        @Volatile
        private var INSTANCE: EtaRepository? = null

        fun getInstance(context: Context): EtaRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EtaRepository(
                    EtaDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }
        }
    }
}
