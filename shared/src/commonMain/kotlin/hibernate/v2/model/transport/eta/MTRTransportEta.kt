package hibernate.v2.model.transport.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.response.eta.MTREta
import hibernate.v2.utils.hongKongTimezone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class MTRTransportEta(
    eta: Long? = null,
    rmkEn: String? = null,
    rmkSc: String? = null,
    rmkTc: String? = null,
    val platform: String? = null,
) : TransportEta(eta, rmkEn, rmkSc, rmkTc, Bound.O, 1) {

    companion object {
        fun fromApiModel(eta: MTREta): MTRTransportEta {
            return MTRTransportEta(
                // YYYY_MM_DD_HH_MM_SS
                eta = eta.time
                    ?.replaceFirst(' ', 'T')
                    ?.toLocalDateTime()
                    ?.toInstant(hongKongTimezone())
                    ?.epochSeconds,
                rmkEn = "",
                rmkSc = "",
                rmkTc = "",
                platform = eta.plat
            )
        }
    }
}
