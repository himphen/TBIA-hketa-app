package hibernate.v2.sunshine.db.kmb

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.Route
import hibernate.v2.sunshine.model.RouteHashable
import hibernate.v2.sunshine.model.TransportRoute
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "kmb_route",
    indices = [Index(value = ["route", "bound", "serviceType"])]
)
data class KmbRouteEntity(
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    @ColumnInfo(name = "service_type")
    val serviceType: String,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
) : Parcelable, RouteHashable {
    companion object {
        fun fromApiModel(route: Route): KmbRouteEntity {
            return KmbRouteEntity(
                routeId = route.routeId,
                bound = route.bound,
                serviceType = route.serviceType,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc
            )
        }

        fun toTransportModel(route: KmbRouteEntity): TransportRoute {
            return TransportRoute(
                routeId = route.routeId,
                bound = route.bound,
                serviceType = route.serviceType,
                origEn = route.origEn,
                origTc = route.origTc,
                origSc = route.origSc,
                destEn = route.destEn,
                destTc = route.destTc,
                destSc = route.destSc,
                stopList = arrayListOf(),
            )
        }
    }

    override fun routeHashId() = routeId + bound.value + serviceType
}