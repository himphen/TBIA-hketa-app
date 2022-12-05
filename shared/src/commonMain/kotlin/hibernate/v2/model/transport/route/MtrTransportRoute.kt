package hibernate.v2.model.transport.route

import dev.icerock.moko.graphics.Color
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.utils.TransportationLanguage

@Parcelize
data class MtrTransportRoute(
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
    val routeInfo: MTRRouteInfo,
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
) {

    override fun compareTo(other: TransportRoute): Int {
        if (other is MtrTransportRoute) {
            val result = routeInfo.nameEn.compareTo(other.routeInfo.nameEn)
            if (result != 0) {
                return result
            }
        }

        return serviceType.compareTo(other.serviceType)
    }

    override fun isSpecialRoute(): Boolean = false

    override fun getCardRouteText(): String = routeInfo.nameTc
}

@Parcelize
data class MTRRouteInfo(
    var nameEn: String,
    var nameTc: String,
    var colorRgba: Long,
) : Parcelable {
    fun getColor(): Color {
        return Color(colorRgba)
    }

    fun getLocalisedName(language: TransportationLanguage): String {
        val localisedName = when (language) {
            TransportationLanguage.EN -> nameEn
            else -> nameTc
        }

        return localisedName
    }
}
