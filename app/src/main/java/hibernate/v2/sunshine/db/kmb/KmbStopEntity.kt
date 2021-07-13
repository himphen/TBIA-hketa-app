package hibernate.v2.sunshine.db.kmb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.kmb.KmbStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Entity(
    tableName = "kmb_stop"
)
data class KmbStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "kmb_stop_id")
    val stopId: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "name_tc")
    val nameTc: String,
    @ColumnInfo(name = "name_sc")
    val nameSc: String,
    val lat: Double,
    val lng: Double,
) {
    companion object {
        fun fromApiModel(stop: KmbStop): KmbStopEntity {
            return KmbStopEntity(
                stopId = stop.stopId,
                nameEn = stop.nameEn,
                nameTc = stop.nameTc,
                nameSc = stop.nameSc,
                lat = stop.lat,
                lng = stop.lng
            )
        }
    }

    fun toTransportModel(): TransportStop {
        return TransportStop(
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