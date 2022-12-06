package hibernate.v2.tbia.model

import hibernate.v2.database.eta.SavedEtaEntity
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.route.TransportRoute

data class BookmarkEdit(
    val entity: SavedEtaEntity,
    val route: TransportRoute,
    val stop: TransportStop,
)
