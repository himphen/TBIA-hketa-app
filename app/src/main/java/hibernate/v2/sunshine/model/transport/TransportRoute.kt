package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorInt
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import kotlinx.parcelize.IgnoredOnParcel
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

    @IgnoredOnParcel
    private var _getDestDirectionText: String? = null

    @IgnoredOnParcel
    private var _getDirectionWithRouteText: String? = null

    @IgnoredOnParcel
    private var _getDirectionSubtitleText: String? = null

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)

    override fun compareTo(other: TransportRoute): Int {
        parseRouteNumber()
        other.parseRouteNumber()

        val companyCompare = company.compareTo(company)
        if (companyCompare != 0) return companyCompare

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
        if (_getDestDirectionText == null) {
            _getDestDirectionText = if (isSpecialRoute()) {
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

        return _getDestDirectionText!!
    }

    open fun getDirectionWithRouteText(context: Context): String {
        if (_getDirectionWithRouteText == null) {
            _getDirectionWithRouteText = if (isSpecialRoute()) {
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

        return _getDirectionWithRouteText!!
    }

    open fun getDirectionSubtitleText(context: Context): String {
        if (_getDirectionSubtitleText == null) {
            _getDirectionSubtitleText = if (isSpecialRoute()) {
                context.getString(
                    R.string.text_add_eta_destination_subtitle_with_sp,
                    serviceType,
                    origTc,
                    destTc
                )
            } else {
                context.getString(
                    R.string.text_add_eta_destination_subtitle,
                    origTc,
                    destTc
                )
            }
        }

        return _getDirectionSubtitleText!!
    }

    open fun getCardRouteText(): String = routeNo

    @ColorInt
    open fun getColor(context: Context, combineNC: Boolean = false): Int =
        context.getColor(
            when (company) {
                Company.KMB -> R.color.brand_color_kmb
                Company.NWFB -> R.color.brand_color_nwfb
                Company.CTB ->
                    if (combineNC) {
                        R.color.brand_color_nwfb
                    } else {
                        R.color.brand_color_ctb
                    }
                Company.GMB -> R.color.brand_color_gmb
                Company.MTR -> R.color.brand_color_mtr
                Company.LRT -> R.color.brand_color_lrt
                Company.UNKNOWN -> R.color.brand_color_kmb
                Company.NLB -> R.color.brand_color_nlb
            }
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransportRoute

        if (routeId != other.routeId) return false
        if (routeNo != other.routeNo) return false
        if (bound != other.bound) return false
        if (serviceType != other.serviceType) return false
        if (origEn != other.origEn) return false
        if (origTc != other.origTc) return false
        if (origSc != other.origSc) return false
        if (destEn != other.destEn) return false
        if (destTc != other.destTc) return false
        if (destSc != other.destSc) return false
        if (company != other.company) return false
        if (routeComponent != other.routeComponent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = routeId.hashCode()
        result = 31 * result + routeNo.hashCode()
        result = 31 * result + bound.hashCode()
        result = 31 * result + serviceType.hashCode()
        result = 31 * result + origEn.hashCode()
        result = 31 * result + origTc.hashCode()
        result = 31 * result + origSc.hashCode()
        result = 31 * result + destEn.hashCode()
        result = 31 * result + destTc.hashCode()
        result = 31 * result + destSc.hashCode()
        result = 31 * result + company.hashCode()
        result = 31 * result + routeComponent.hashCode()
        return result
    }


    companion object {
        fun notFoundRoute() = TransportRoute(
            company = Company.UNKNOWN,
            routeId = "-",
            routeNo = "-",
            bound = Bound.UNKNOWN,
            serviceType = "",
            origEn = "-", // TODO
            origTc = "-", // TODO
            origSc = "-", // TODO
            destEn = "-", // TODO
            destTc = "-", // TODO
            destSc = "-",
        )
    }
}

@Parcelize
data class RouteComponent(
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
