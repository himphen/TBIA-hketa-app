package hibernate.v2.database.ctb

import hibernate.v2.api.model.transport.Company
import hibernate.v2.model.transport.TransportStop
import hibernatev2database.Ctb_stop

data class CtbStopEntity(
    val stopId: String,
    val nameEn: String,
    val nameTc: String,
    val nameSc: String,
    val lat: Double,
    val lng: Double,
    val geohash: String,
) {
    companion object {
        fun convertFrom(item: Ctb_stop) = CtbStopEntity(
            stopId = item.ctb_stop_id,
            nameEn = item.name_en,
            nameTc = item.name_tc,
            nameSc = item.name_sc,
            lat = item.lat,
            lng = item.lng,
            geohash = item.geohash,
        )
    }

    fun toTransportModel(): TransportStop {
        return TransportStop(
            company = Company.NWFB,
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
            company = Company.NWFB,
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
