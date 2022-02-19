package hibernate.v2.sunshine.model.transport

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.model.transport.gmb.GmbEta
import hibernate.v2.api.model.transport.nlb.NLBEta
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.DateUtil.formatString
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

open class TransportEta(
    val eta: Date?,
    val rmkEn: String?,
    val rmkSc: String?,
    val rmkTc: String?,
    val bound: Bound?,
    val seq: Int?,
) {
    private val etaWithoutSecond by lazy {
        eta?.let {
            // Ignore second and millisecond
            GregorianCalendar().apply {
                time = eta
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    val etaFormattedInHHMM by lazy {
        eta.formatString(DateFormat.HH_MM.value)
    }

    companion object {
        fun fromApiModel(eta: BusEta): TransportEta {
            return TransportEta(
                eta = DateUtil.getDate(
                    eta.eta,
                    DateFormat.ISO_WITHOUT_MS.value
                ),
                rmkEn = eta.rmkEn,
                rmkSc = eta.rmkSc,
                rmkTc = eta.rmkTc,
                bound = eta.bound,
                seq = eta.seq
            )
        }

        fun fromApiModel(eta: GmbEta): TransportEta {
            return TransportEta(
                eta = DateUtil.getDate(
                    eta.timestamp,
                    DateFormat.ISO.value
                ),
                rmkEn = eta.rmkEn,
                rmkSc = eta.rmkSc,
                rmkTc = eta.rmkTc,
                bound = Bound.O,
                seq = eta.seq
            )
        }

        fun fromApiModel(eta: NLBEta, seq: Int?): TransportEta {
            return TransportEta(
                eta = DateUtil.getDate(
                    eta.estimatedArrivalTime,
                    DateFormat.YYYY_MM_DD_HH_MM_SS.value
                ),
                rmkEn = "",
                rmkSc = "",
                rmkTc = "",
                bound = Bound.O,
                seq = seq
            )
        }
    }

    open fun getEtaMinuteText(default: String = ""): Pair<Boolean, String> {
        etaWithoutSecond?.let { etaWithoutSecond ->
            val minutes = DateUtil.getTimeDiffInMin(etaWithoutSecond.time, Date())
            if (minutes < 0) {
                return false to default
            }

            return true to (minutes + 1).toString()
        }

        return false to default
    }
}

/**
 * Filter bus terminal stops in circular line
 */
fun TransportEta.filterCircularStop(isCircular: Boolean, stop: TransportStop): Boolean {
    return if (isCircular) {
        if (stop.seq == 1) {
            seq == 1
        } else {
            seq != 1
        }
    } else {
        true
    }
}
