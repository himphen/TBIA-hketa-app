package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteStopList(
    val stopList: MutableList<TransportStop>,
    val route: TransportRoute
) : Parcelable