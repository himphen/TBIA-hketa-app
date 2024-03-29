package hibernate.v2.database.eta

import hibernate.v2.database.DatabaseDriverFactory

class EtaOrderDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = databaseDriverFactory.createDatabase()
    private val queries = database.etaDaoQueries

    fun getEtaOrder(): List<SavedEtaOrderEntity> {
        return queries.getAllEtaOrder().executeAsList().map {
            SavedEtaOrderEntity.convertFrom(it)
        }
    }

    fun add(list: List<SavedEtaOrderEntity>) {
        queries.transaction {
            list.forEach {
                queries.addEtaOrder(
                    it.id?.toLong(),
                    it.position.toLong()
                )
            }
        }
    }

    fun clearAll() {
        queries.clearAllEtaOrder()
    }
}
