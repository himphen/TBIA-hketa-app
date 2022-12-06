package hibernate.v2.database.nlb

import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportStop
import hibernatev2database.Nlb_stop

data class NlbStopEntity(
    val stopId: String,
    val nameEn: String,
    val nameTc: String,
    val nameSc: String,
    val lat: Double,
    val lng: Double,
    val geohash: String,
) {
    companion object {
        fun convertFrom(stop: Nlb_stop): NlbStopEntity {
            return NlbStopEntity(
                stopId = stop.nlb_stop_id,
                nameEn = stop.name_en,
                nameTc = stop.name_tc,
                nameSc = stop.name_sc,
                lat = stop.lat,
                lng = stop.lng,
                geohash = stop.geohash,
            )
        }
    }

    fun toTransportModel(): TransportStop {
        return TransportStop(
            company = Company.NLB,
            stopId = stopId,
            nameEn = nameEn,
            nameTc = nameTc,
            nameSc = nameSc,
            lat = lat,
            lng = lng,
            seq = null,
        )
    }

    fun toTransportModelWithSeq(seq: Int): TransportStop {
        return TransportStop(
            company = Company.NLB,
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
