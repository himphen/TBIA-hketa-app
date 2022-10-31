package hibernate.v2.database.eta

import hibernatev2database.Saved_eta_order

data class SavedEtaOrderEntity(
    val id: Int? = null,
    val position: Int
) {
    companion object {
        fun convertFrom(item: Saved_eta_order) = SavedEtaOrderEntity(
            id = item.saved_eta_order_id.toInt(),
            position = item.position.toInt(),
        )
    }
}
