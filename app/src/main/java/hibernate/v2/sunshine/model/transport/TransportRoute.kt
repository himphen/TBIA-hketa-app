package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorRes
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import kotlinx.parcelize.Parcelize

// TODO change to data class
@Parcelize
open class TransportRoute(
    open val routeId: String,
    open val routeNo: String,
    open val bound: Bound,
    open val serviceType: String,
    open val origEn: String,
    open val origTc: String,
    open val origSc: String,
    open val destEn: String,
    open val destTc: String,
    open val destSc: String,
    open val company: Company,

    var routeComponent: RouteComponent = RouteComponent(),
) : TransportHashable, Comparable<TransportRoute>, Parcelable {

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: TransportRoute): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val routeCompare = routeComponent.compareTo(other.routeComponent)
        if (routeCompare != 0) return routeCompare

        val serviceTypeCompare = serviceType.compareTo(other.serviceType)
        if (serviceTypeCompare != 0) return serviceTypeCompare

        return bound.compareTo(other.bound)
    }

    private fun parseRouteNumber() {
        if (routeComponent.parsed) return

        Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeNo)?.let { match ->
            routeComponent.routePrefix = match.destructured.component1()
            routeComponent.routeNumber = match.destructured.component2().toInt()
            routeComponent.routeSuffix = match.destructured.component3()
        }

        routeComponent.parsed = true
    }

    open fun isSpecialRoute(): Boolean = serviceType != "1"

    fun getDestDirectionText(context: Context): String {
        return if (isSpecialRoute()) {
            context.getString(
                R.string.text_settings_eta_destination_with_sp,
                destTc,
                serviceType
            )
        } else {
            context.getString(
                R.string.text_settings_eta_destination,
                destTc
            )
        }
    }

    open fun getDirectionWithRouteText(context: Context): String {
        return if (isSpecialRoute()) {
            context.getString(
                R.string.text_add_eta_destination_with_sp,
                routeNo,
                serviceType,
                origTc,
                destTc
            )

        } else {
            context.getString(
                R.string.text_add_eta_destination,
                routeNo,
                origTc,
                destTc
            )
        }
    }

    @ColorRes
    fun getColor(combineNC: Boolean = false): Int = when (company) {
        Company.KMB -> R.color.brand_color_kmb
        Company.NWFB -> R.color.brand_color_nwfb
        Company.CTB ->
            if (combineNC) {
                R.color.brand_color_nwfb
            } else {
                R.color.brand_color_ctb
            }
        Company.GMB -> R.color.brand_color_gmb
        else -> R.color.brand_color_kmb
    }
}

@Parcelize
class RouteComponent(
    var routePrefix: String = "",
    var routeNumber: Int = 0,
    var routeSuffix: String = "",
    var parsed: Boolean = false,
) : Comparable<RouteComponent>, Parcelable {
    override fun compareTo(other: RouteComponent): Int {
        val routePrefixCompare = routePrefix.compareTo(other.routePrefix)
        if (routePrefixCompare != 0) return routePrefixCompare

        val routeCompare = routeNumber.compareTo(other.routeNumber)
        if (routeCompare != 0) return routeCompare

        val routeSuffixCompare = routeSuffix.compareTo(other.routeSuffix)
        if (routeSuffixCompare != 0) return routeSuffixCompare

        return 0
    }

}