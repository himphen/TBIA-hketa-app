package hibernate.v2.model.transport.route

import dev.icerock.moko.graphics.Color
import dev.icerock.moko.parcelize.IgnoredOnParcel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import hibernate.v2.MR
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportHashable
import hibernate.v2.utils.KMMContext
import hibernate.v2.utils.LanguageUtils.isLangEnglish
import hibernate.v2.utils.localized

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
    private var destDirectionText: String? = null

    @IgnoredOnParcel
    private var directionWithRouteText: String? = null

    @IgnoredOnParcel
    private var directionSubtitleText: String? = null

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

    open fun getCardRouteText(): String = routeNo

    fun getDestDirectionText(context: KMMContext): String {
        if (destDirectionText == null) {
            destDirectionText = if (isSpecialRoute()) {
                StringDesc.ResourceFormatted(
                    MR.strings.text_settings_eta_destination_with_sp,
                    getLocalisedDest(context),
                    serviceType
                ).localized(context)
            } else {
                StringDesc.ResourceFormatted(
                    MR.strings.text_settings_eta_destination,
                    getLocalisedDest(context)
                ).localized(context)
            }
        }

        return destDirectionText!!
    }

    fun getDirectionSubtitleText(context: KMMContext): String {
        if (directionSubtitleText == null) {
            directionSubtitleText = if (isSpecialRoute()) {
                StringDesc.ResourceFormatted(
                    MR.strings.text_add_eta_destination_subtitle_with_sp,
                    serviceType,
                    getLocalisedOrig(context),
                    getLocalisedDest(context)
                ).localized(context)
            } else {
                StringDesc.ResourceFormatted(
                    MR.strings.text_add_eta_destination_subtitle,
                    getLocalisedOrig(context),
                    getLocalisedDest(context)
                ).localized(context)
            }
        }

        return directionSubtitleText!!
    }

    fun getLocalisedOrig(context: KMMContext): String {
        val localisedOrig = if (isLangEnglish(context)) {
            origEn
        } else {
            origTc
        }

        return localisedOrig
    }

    fun getLocalisedDest(context: KMMContext): String {
        val localisedDest = if (isLangEnglish(context)) {
            destEn
        } else {
            destTc
        }

        return localisedDest
    }

    fun getDirectionWithRouteText(context: KMMContext): String {
        if (directionWithRouteText == null) {
            directionWithRouteText = if (isSpecialRoute()) {
                StringDesc.ResourceFormatted(
                    MR.strings.text_add_eta_destination_with_sp,
                    routeNo,
                    serviceType,
                    getLocalisedOrig(context),
                    getLocalisedDest(context)
                ).localized(context)
            } else {
                StringDesc.ResourceFormatted(
                    MR.strings.text_add_eta_destination,
                    routeNo,
                    getLocalisedOrig(context),
                    getLocalisedDest(context)
                ).localized(context)
            }
        }

        return directionWithRouteText!!
    }

    fun getColor(): Color {
        if (this is MtrTransportRoute) {
            return routeInfo.getColor()
        }

        if (this is LrtTransportRoute) {
            return routeInfo.getColor()
        }

        return when (company) {
            Company.KMB -> MR.colors.brand_color_kmb.color
            Company.CTB -> MR.colors.brand_color_ctb.color
            Company.GMB -> MR.colors.brand_color_gmb.color
            Company.MTR -> MR.colors.brand_color_mtr.color
            Company.LRT -> MR.colors.brand_color_lrt.color
            Company.UNKNOWN -> MR.colors.brand_color_kmb.color
            Company.NLB -> MR.colors.brand_color_nlb.color
        }
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
