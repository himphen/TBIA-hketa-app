package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteStopList(
    val stopList: MutableList<Stop>,
    val route: Route
) : Parcelable