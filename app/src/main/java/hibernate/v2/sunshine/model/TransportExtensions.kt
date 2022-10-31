package hibernate.v2.sunshine.model

import android.content.Context
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.LrtTransportRoute
import hibernate.v2.model.transport.route.MtrTransportRoute
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.util.GeneralUtils.isLangEnglish

fun EtaType.name(context: Context): String = when (this) {
    EtaType.KMB -> context.getString(R.string.add_eta_brand_selection_kmb_btn)
    EtaType.NWFB -> context.getString(R.string.add_eta_brand_selection_nwfb_btn)
    EtaType.CTB -> context.getString(R.string.add_eta_brand_selection_ctb_btn)
    EtaType.GMB_HKI -> context.getString(R.string.add_eta_brand_selection_gmb_hki_btn)
    EtaType.GMB_KLN -> context.getString(R.string.add_eta_brand_selection_gmb_kln_btn)
    EtaType.GMB_NT -> context.getString(R.string.add_eta_brand_selection_gmb_nt_btn)
    EtaType.MTR -> context.getString(R.string.add_eta_brand_selection_mtr_btn)
    EtaType.LRT -> context.getString(R.string.add_eta_brand_selection_lrt_btn)
    EtaType.NLB -> context.getString(R.string.add_eta_brand_selection_nlb_btn)
}

fun EtaType.color(context: Context): Int = when (this) {
    EtaType.KMB -> context.getColor(R.color.brand_color_kmb)
    EtaType.NWFB -> context.getColor(R.color.brand_color_nwfb)
    EtaType.GMB_HKI, EtaType.GMB_KLN, EtaType.GMB_NT -> context.getColor(R.color.brand_color_gmb)
    EtaType.CTB -> context.getColor(R.color.brand_color_ctb)
    EtaType.MTR -> context.getColor(R.color.brand_color_mtr)
    EtaType.LRT -> context.getColor(R.color.brand_color_lrt)
    EtaType.NLB -> context.getColor(R.color.brand_color_nlb)
}

fun TransportStop.getLocalisedName(context: Context): String {
    val localisedName = if (isLangEnglish(context)) {
        nameEn
    } else {
        nameTc
    }

    if (company == Company.MTR || company == Company.LRT) {
        return context.getString(R.string.text_eta_card_classic_mtr_station, localisedName)
    }

    return localisedName
}

fun TransportRoute.getDestDirectionText(context: Context): String {
    if (_getDestDirectionText == null) {
        _getDestDirectionText = if (isSpecialRoute()) {
            context.getString(
                R.string.text_settings_eta_destination_with_sp,
                getLocalisedDest(context),
                serviceType
            )
        } else {
            context.getString(
                R.string.text_settings_eta_destination,
                getLocalisedDest(context)
            )
        }
    }

    return _getDestDirectionText!!
}

fun TransportRoute.getDirectionWithRouteText(context: Context): String {
    if (_getDirectionWithRouteText == null) {
        _getDirectionWithRouteText = if (isSpecialRoute()) {
            context.getString(
                R.string.text_add_eta_destination_with_sp,
                routeNo,
                serviceType,
                getLocalisedOrig(context),
                getLocalisedDest(context)
            )
        } else {
            context.getString(
                R.string.text_add_eta_destination,
                routeNo,
                getLocalisedOrig(context),
                getLocalisedDest(context)
            )
        }
    }

    return _getDirectionWithRouteText!!
}

fun TransportRoute.getDirectionSubtitleText(context: Context): String {
    if (_getDirectionSubtitleText == null) {
        _getDirectionSubtitleText = if (isSpecialRoute()) {
            context.getString(
                R.string.text_add_eta_destination_subtitle_with_sp,
                serviceType,
                getLocalisedOrig(context),
                getLocalisedDest(context)
            )
        } else {
            context.getString(
                R.string.text_add_eta_destination_subtitle,
                getLocalisedOrig(context),
                getLocalisedDest(context)
            )
        }
    }

    return _getDirectionSubtitleText!!
}

fun TransportRoute.getCardRouteText(): String = routeNo

fun TransportRoute.getLocalisedOrig(context: Context): String {
    val localisedOrig = if (isLangEnglish(context)) {
        origEn
    } else {
        origTc
    }

    return localisedOrig
}

fun TransportRoute.getLocalisedDest(context: Context): String {
    val localisedDest = if (isLangEnglish(context)) {
        destEn
    } else {
        destTc
    }

    return localisedDest
}

fun TransportRoute.getColor(context: Context, combineNC: Boolean = false): Int {
    if (this is MtrTransportRoute) {
        return routeInfo.color.colorInt()
    }

    if (this is LrtTransportRoute) {
        return routeInfo.color.colorInt()
    }

    return context.getColor(
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
}
