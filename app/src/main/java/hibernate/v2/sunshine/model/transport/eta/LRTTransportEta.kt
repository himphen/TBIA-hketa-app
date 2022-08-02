package hibernate.v2.sunshine.model.transport.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.response.eta.LRTEta
import hibernate.v2.sunshine.util.DateFormatPattern
import hibernate.v2.sunshine.util.DateUtil
import java.util.Calendar
import java.util.Date

class LRTTransportEta(
    date: Date?,
    rmkEn: String?,
    rmkSc: String?,
    rmkTc: String?,
    val platform: String
) : TransportEta(date, rmkEn, rmkSc, rmkTc, Bound.O, 1) {

    companion object {
        fun fromApiModel(eta: LRTEta, platform: String, systemTime: String?): LRTTransportEta {
            var rmkEn = ""
            var rmkTc = ""
            var date: Date? = null

            if (eta.timeEn.contains(" min")) {
                DateUtil.getDate(systemTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS.value)?.let {
                    val minute = eta.timeEn.replace(" min", "").toIntOrNull() ?: 0
                    val target = Calendar.getInstance()
                    target.time = it
                    target.add(Calendar.MINUTE, minute)
                    date = target.time
                }
            } else if (eta.timeEn.lowercase() == "departing" || eta.timeEn.lowercase() == "arriving") {
                DateUtil.getDate(systemTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS.value)?.let {
                    val target = Calendar.getInstance()
                    target.time = it
                    target.add(Calendar.MINUTE, 1)
                    date = target.time
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
