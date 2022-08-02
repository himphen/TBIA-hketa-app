package hibernate.v2.sunshine.model.transport.route

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorInt
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.util.GeneralUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class LRTTransportRoute(
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
    val routeInfo: LRTRouteInfo,
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
),
    Parcelable {

    override fun isSpecialRoute(): Boolean = false

    override fun getDirectionWithRouteText(context: Context): String {
        val localisedDesc = if (GeneralUtils.isLangEnglish(context)) {
            destEn
        } else {
            destTc
        }
        val localisedOrig = if (GeneralUtils.isLangEnglish(context)) {
            origEn
        } else {
            destTc
        }

        return context.getString(
            R.string.text_add_eta_destination_train,
            routeNo,
            localisedOrig,
            localisedDesc
        )
    }

    override fun getCardRouteText(): String = routeNo

    @ColorInt
    override fun getColor(context: Context, combineNC: Boolean): Int =
        routeInfo.color
}

@Parcelize
data class LRTRouteInfo(
    @ColorInt var color: Int,
) : Parcelable
