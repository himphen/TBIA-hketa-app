package hibernate.v2.model.searchmap

import hibernate.v2.database.ctb.CtbStopEntity
import hibernate.v2.database.gmb.GmbStopEntity
import hibernate.v2.database.kmb.KmbStopEntity
import hibernate.v2.database.nlb.NlbStopEntity
import hibernate.v2.model.Card
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.utils.TransportationLanguage

actual data class SearchMapStop(
    actual val lat: Double,
    actual val lng: Double,
    actual val nameEn: String,
    actual val nameSc: String,
    actual val nameTc: String,
    actual val stopId: String,
    actual val etaType: EtaType,
    actual var mapRouteList: List<Card.EtaCard> = emptyList(),
) {
    actual fun getLocalisedName(transportationLanguage: TransportationLanguage): String {
        return "Not yet implemented"
    }

    actual companion object {
        actual fun fromStopEntity(it: KmbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.KMB
            )
        }

        actual fun fromStopEntity(it: CtbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.NWFB
            )
        }

        actual fun fromStopEntity(it: GmbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.GMB_HKI
            )
        }

        actual fun fromStopEntity(it: NlbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.NLB
            )
        }
    }

    actual fun toTransportModelWithSeq(seq: Int): TransportStop {
        return TransportStop(
            company = etaType.company(),
            stopId = stopId,
            nameEn = nameEn,
            nameTc = nameTc,
            nameSc = nameSc,
            lat = lat,
            lng = lng,
            seq = seq,
        )
    }
}