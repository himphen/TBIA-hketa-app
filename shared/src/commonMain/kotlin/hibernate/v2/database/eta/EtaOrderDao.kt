package hibernate.v2.database.eta

import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.DatabaseFactory
import hibernatev2database.Saved_eta_order

class EtaOrderDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val driver = databaseDriverFactory.createDriver()
    private val database = DatabaseFactory.createDatabase(driver)
    private val queries = database.etaDaoQueries

    fun getEtaOrder(): List<Saved_eta_order> {
        return queries.getAllEtaOrder().executeAsList()
    }

    fun add(list: List<SavedEtaOrderEntity>) {
        queries.transaction {
            list.forEach { queries.addEtaOrder(convertFrom(it)) }
        }
    }

    fun clearAll() {
        queries.clearAllEtaOrder()
    }

    private fun convertFrom(item: SavedEtaOrderEntity) = Saved_eta_order(
        saved_eta_order_id = 0,
        position = item.position,
    )

}
