package hibernate.v2.database.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.DatabaseFactory
import hibernate.v2.database.runGettingLastId

class EtaDao(databaseDriverFactory: DatabaseDriverFactory) {

    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.etaDaoQueries

    fun getAllKmbEtaWithOrdering(): List<EtaKmbDetails> {
        return queries.getAllKmbEtaWithOrdering().executeAsList().map {
            EtaKmbDetails.convertFrom(it)
        }
    }

    fun getAllCtbEtaWithOrdering(): List<EtaCtbDetails> {
        return queries.getAllCtbEtaWithOrdering().executeAsList().map {
            EtaCtbDetails.convertFrom(it)
        }
    }

    fun getAllGmbEtaWithOrdering(): List<EtaGmbDetails> {
        return queries.getAllGmbEtaWithOrdering().executeAsList().map {
            EtaGmbDetails.convertFrom(it)
        }
    }

    fun getAllMtrEtaWithOrdering(): List<EtaMtrDetails> {
        return queries.getAllMtrEtaWithOrdering().executeAsList().map {
            EtaMtrDetails.convertFrom(it)
        }
    }

    fun getAllLrtEtaWithOrdering(): List<EtaLrtDetails> {
        return queries.getAllLrtEtaWithOrdering().executeAsList().map {
            EtaLrtDetails.convertFrom(it)
        }
    }

    fun getAllNlbEtaWithOrdering(): List<EtaNlbDetails> {
        return queries.getAllNlbEtaWithOrdering().executeAsList().map {
            EtaNlbDetails.convertFrom(it)
        }
    }

    fun hasEtaInDb(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int,
        company: Company,
    ): Boolean {
        return queries.getSingleEta(
            stopId,
            routeId,
            bound,
            serviceType,
            seq.toLong(),
            company
        ).executeAsOneOrNull() != null
    }

    fun addEta(item: SavedEtaEntity): Int {
        return database.runGettingLastId {
            queries.addEta(
                saved_eta_id = item.id?.toLong(),
                saved_eta_company = item.company,
                saved_eta_stop_id = item.stopId,
                saved_eta_route_id = item.routeId,
                saved_eta_route_bound = item.bound,
                saved_eta_service_type = item.serviceType,
                saved_eta_seq = item.seq.toLong()
            )
        }.toInt()
    }

    fun addEta(list: List<SavedEtaEntity>) {
        queries.transaction {
            list.forEach { item ->
                queries.addEta(
                    saved_eta_id = item.id?.toLong(),
                    saved_eta_company = item.company,
                    saved_eta_stop_id = item.stopId,
                    saved_eta_route_id = item.routeId,
                    saved_eta_route_bound = item.bound,
                    saved_eta_service_type = item.serviceType,
                    saved_eta_seq = item.seq.toLong()
                )
            }
        }
    }

    fun clearEta(id: Int) {
        queries.clearEtaById(id.toLong())
    }

    fun clearEta(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: Int
    ) {
        queries.clearEta(
            stopId,
            routeId,
            bound,
            serviceType,
            seq.toLong()
        )
    }

    fun clearAllEta() {
        queries.clearAllEta()
    }
}
