package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
import hibernate.v2.sunshine.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class GmbTransportRoute(
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
    val region: GmbRegion
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
        if (other is GmbTransportRoute) {
            val result = region.ordering.compareTo(other.region.ordering)
            if (result != 0) {
                return result
            }
        }

        return super.compareTo(other)
    }

    override fun isSpecialRoute(): Boolean = false

    fun getRegionName(context: Context): String {
        return when (region) {
            GmbRegion.HKI -> context.getString(R.string.text_gmb_region_hki)
            GmbRegion.KLN -> context.getString(R.string.text_gmb_region_kln)
            GmbRegion.NT -> context.getString(R.string.text_gmb_region_nt)
            GmbRegion.UNKNOWN -> ""
        }
    }

    override fun getDirectionWithRouteText(context: Context): String {
        return context.getString(
            R.string.text_add_eta_destination,
            "${getRegionName(context)} $routeNo",
            origTc,
            destTc
        )
    }

}