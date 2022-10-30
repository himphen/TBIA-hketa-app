package hibernate.v2.model.transport.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.response.eta.LrtEta
import hibernate.v2.utils.hongKongTimezone
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class LRTTransportEta(
    date: LocalDateTime?,
    rmkEn: String?,
    rmkSc: String?,
    rmkTc: String?,
    val platform: String
) : TransportEta(date, rmkEn, rmkSc, rmkTc, Bound.O, 1) {

    companion object {
        fun fromApiModel(eta: LrtEta, platform: String, systemTime: String?): LRTTransportEta {
            var rmkEn = ""
            var rmkTc = ""
            var date: LocalDateTime? = null

            // YYYY_MM_DD_HH_MM_SS
            if (eta.timeEn.contains(" min")) {
                systemTime?.replaceFirst(' ', 'T')?.toLocalDateTime()?.let {
                    val minute = eta.timeEn.replace(" min", "").toIntOrNull() ?: 0
                    date = it
                        .toInstant(hongKongTimezone())
                        .plus(minute, DateTimeUnit.MINUTE)
                        .toLocalDateTime(hongKongTimezone())
                }
            } else if (eta.timeEn.lowercase() == "departing" || eta.timeEn.lowercase() == "arriving") {
                systemTime?.replaceFirst(' ', 'T')?.toLocalDateTime()?.let {
                    date = it
                        .toInstant(hongKongTimezone())
                        .plus(1, DateTimeUnit.MINUTE)
                        .toLocalDateTime(hongKongTimezone())
                }

                rmkEn = eta.timeEn
                rmkTc = eta.timeCh
            }

            return LRTTransportEta(
                date,
                rmkEn = rmkEn,
                rmkSc = rmkTc,
                rmkTc = rmkTc,
                platform = platform
            )
        }
    }
}
