package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class MTRTransportRoute(
    override val routeId: String,
    override val routeNo: String,
    override val bound: Bound,
    override val serviceType: String,
    override val origEn: String,
    override val origTc: String,
    override val origSc: String,
    override val destEn: String,
    override val destTc: String,
    override val destSc: String,
    override val company: Company,
    val routeInfo: RouteInfo,
) : TransportRoute(
    routeId,
    routeNo,
    bound,
    serviceType,
    origEn,
    origTc,
    origSc,
    destEn,
    destTc,
    destSc,
    company,
), Parcelable {

    override fun compareTo(other: TransportRoute): Int {
        if (other is MTRTransportRoute) {
            val result = routeInfo.nameEn.compareTo(other.routeInfo.nameEn)
            if (result != 0) {
                return result
            }
        }

        return serviceType.compareTo(other.serviceType)
    }

    override fun isSpecialRoute(): Boolean = false

    override fun getDirectionWithRouteText(context: Context): String {
        return context.getString(
            R.string.text_add_eta_destination,
            routeInfo.nameTc,
            origTc,
            destTc
        )
    }

}