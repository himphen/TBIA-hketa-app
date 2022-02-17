package hibernate.v2.sunshine.db.gmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.gmb.GmbStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Entity(
    tableName = "gmb_stop"
)
data class GmbStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "gmb_stop_id")
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
        fun fromApiModel(stop: GmbStop): GmbStopEntity {
            return GmbStopEntity(
                stopId = stop.stopId.toString(),
                nameEn = stop.nameEn,
                nameTc = stop.nameTc,
                nameSc = stop.nameSc,
                lat = stop.lat,
                lng = stop.lng,
                geohash = stop.geohash,
            )
        }
    }

    fun toTransportModel(): TransportStop {
        return TransportStop(
            company = Company.GMB,
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
            company = Company.GMB,
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
