package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.nc.NCStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Entity(
    tableName = "nc_stop"
)
data class NCStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "nc_stop_id")
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
        fun fromApiModel(stop: NCStop) = NCStopEntity(
            stopId = stop.stopId,
            nameEn = stop.nameEn,
            nameTc = stop.nameTc,
            nameSc = stop.nameSc,
            lat = stop.lat.toDoubleOrNull() ?: 0.0,
            lng = stop.lng.toDoubleOrNull() ?: 0.0
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