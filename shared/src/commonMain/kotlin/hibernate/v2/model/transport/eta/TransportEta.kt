package hibernate.v2.model.transport.eta

import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.BusEta
import hibernate.v2.api.model.transport.gmb.GmbEta
import hibernate.v2.api.model.transport.nlb.NlbEta
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.utils.TransportationLanguage
import hibernate.v2.utils.getTimeDiffInMin
import hibernate.v2.utils.hongKongTimezone
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

open class TransportEta(
    val eta: Long?,
    val rmkEn: String?,
    val rmkSc: String?,
    val rmkTc: String?,
    val bound: Bound?,
    val seq: Int?,
) {

    fun getLocalisedRmk(language: TransportationLanguage): String? {
        val localisedRmk = when (language) {
            TransportationLanguage.EN -> rmkEn
            else -> rmkTc
        }

        return localisedRmk
    }

    private val etaWithoutSecond by lazy {
        eta?.let {
            it / 60 * 60
        }
    }

    companion object {
        fun fromApiModel(eta: BusEta): TransportEta {
            return TransportEta(
                // ISO_WITHOUT_MS
                eta = eta.eta?.toInstant()?.epochSeconds,
                rmkEn = eta.rmkEn,
                rmkSc = eta.rmkSc,
                rmkTc = eta.rmkTc,
                bound = eta.bound,
                seq = eta.seq
            )
        }

        fun fromApiModel(eta: GmbEta): TransportEta {
            return TransportEta(
                // ISO
                eta = eta.timestamp?.toInstant()?.toEpochMilliseconds(),
                rmkEn = eta.rmkEn,
                rmkSc = eta.rmkSc,
                rmkTc = eta.rmkTc,
                bound = Bound.O,
                seq = eta.seq
            )
        }

        fun fromApiModel(eta: NlbEta, seq: Int?): TransportEta {
            return TransportEta(
                // YYYY_MM_DD_HH_MM_SS
                eta = eta.estimatedArrivalTime
                    ?.replaceFirst(' ', 'T')
                    ?.toLocalDateTime()
                    ?.toInstant(
                        hongKongTimezone()
                    )
                    ?.epochSeconds,
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
            val minutes = getTimeDiffInMin(
                Instant.fromEpochSeconds(etaWithoutSecond),
                Clock.System.now()
            )
            if (minutes <= 0) {
                return true to "< 1"
            }

            return true to minutes.toString()
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
