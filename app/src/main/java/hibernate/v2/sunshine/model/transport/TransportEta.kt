package hibernate.v2.sunshine.model.transport

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.model.transport.gmb.GmbEta
import hibernate.v2.api.model.transport.nlb.NLBEta
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

open class TransportEta(
    val eta: Date?,
    val rmkEn: String?,
    val rmkSc: String?,
    val rmkTc: String?,
    val bound: Bound?,
    val seq: Int?,
) {

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
        eta?.let { etaDate ->
            val minutes = DateUtil.getTimeDiffInMin(
                etaDate,
                Date()
            )
            return true to (minutes + 1).toString()
        }

        return false to default
    }
}