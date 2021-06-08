package hibernate.v2.sunshine.repository

import android.content.Context
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.EtaDatabase
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity

class EtaRepository private constructor(etaDb: EtaDatabase) {

    private val etaDao = etaDb.etaDao()
    private val etaOrderDb = etaDb.etaOrderDao()

    suspend fun getEtaList(): List<EtaEntity> = etaDao.get()

    suspend fun getEtaList(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    ): List<EtaEntity> = etaDao.get(
        stopId,
        routeId,
        bound,
        serviceType,
        seq
    )

    suspend fun addEta(entity: EtaEntity) {
        etaDao.add(entity)
    }

    suspend fun clearEta(entity: EtaEntity) {
        etaDao.clear(
            entity.stopId,
            entity.routeId,
            entity.bound,
            entity.serviceType,
            entity.seq
        )
    }

    suspend fun clearAllEta() {
        etaDao.clearAll()
    }

    suspend fun getEtaOrderList() = etaOrderDb.get()

    suspend fun updateEtaOrderList(entityList: List<EtaOrderEntity>) {
        etaOrderDb.clearAll()
        etaOrderDb.add(entityList)
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
