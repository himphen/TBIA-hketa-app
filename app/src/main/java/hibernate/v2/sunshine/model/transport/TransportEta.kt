package hibernate.v2.sunshine.model.transport

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.model.transport.gmb.GmbEta
import hibernate.v2.sunshine.util.DateFormat
import hibernate.v2.sunshine.util.DateUtil
import java.util.Date

class TransportEta(
    val eta: Date? = null,
    val rmkEn: String? = null,
    val rmkSc: String? = null,
    val rmkTc: String? = null,
    val bound: Bound? = null,
    val seq: Int? = null,
) {

    companion object {
        fun fromApiModel(eta: BusEta): TransportEta {
            return TransportEta(
                eta = DateUtil.getDate(
                    eta.dataTimestamp,
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
                eta = DateUtil.getDate(eta.timestamp, DateFormat.ISO.value),
                rmkEn = eta.rmkEn,
                rmkSc = eta.rmkSc,
                rmkTc = eta.rmkTc,
                bound = Bound.O,
                seq = eta.seq
            )
        }
    }
}