package hibernate.v2.sunshine.model

import android.os.Parcelable
import hibernate.v2.api.model.Bound
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportRoute(
    val routeId: String,
    val bound: Bound,
    val serviceType: String,
    val origEn: String,
    val origTc: String,
    val origSc: String,
    val destEn: String,
    val destTc: String,
    val destSc: String,
    val stopList: MutableList<TransportStop>
) : Parcelable, RouteHashable {
    override fun routeHashId(): String {
        return routeId + bound.value + serviceType
    }
}