package hibernate.v2.sunshine.model.transport.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.response.eta.MTREta
import hibernate.v2.sunshine.util.DateFormatPattern
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class MTRTransportEta(
    eta: Date? = null,
    rmkEn: String? = null,
    rmkSc: String? = null,
    rmkTc: String? = null,
    val platform: String? = null,
) : TransportEta(eta, rmkEn, rmkSc, rmkTc, Bound.O, 1) {

    companion object {
        fun fromApiModel(eta: MTREta): MTRTransportEta {
            return MTRTransportEta(
                eta = DateUtil.getDate(
                    eta.time,
                    DateFormatPattern.YYYY_MM_DD_HH_MM_SS.value
                ),
                rmkEn = "",
                rmkSc = "",
                rmkTc = "",
                platform = eta.plat
            )
        }
    }
}
