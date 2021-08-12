package hibernate.v2.sunshine.db.mtr

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.mtr.MTRStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Entity(
    tableName = "mtr_stop"
)
data class MTRStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "mtr_stop_id")
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
        fun fromApiModel(stop: MTRStop): MTRStopEntity {
            return MTRStopEntity(
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
            company = Company.MTR,
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
            company = Company.MTR,
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