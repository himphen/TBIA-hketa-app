package hibernate.v2.sunshine.db.nlb

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nlb.NlbStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Keep
@Entity(
    tableName = "nlb_stop"
)
data class NLBStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "nlb_stop_id")
    val stopId: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "name_tc")
    val nameTc: String,
    @ColumnInfo(name = "name_sc")
    val nameSc: String,
    val lat: Double,
    val lng: Double,
    val geohash: String,
) {
    companion object {
        fun fromApiModel(stop: NlbStop): NLBStopEntity {
            return NLBStopEntity(
                stopId = stop.stopId,
                nameEn = stop.nameEn,
                nameTc = stop.nameTc,
                nameSc = stop.nameSc,
                lat = stop.lat.toDoubleOrNull() ?: 0.0,
                lng = stop.lng.toDoubleOrNull() ?: 0.0,
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
