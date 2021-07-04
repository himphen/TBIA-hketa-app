package hibernate.v2.sunshine.db.nc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.kmb.KmbStop
import hibernate.v2.api.model.nc.NCStop
import hibernate.v2.sunshine.model.transport.TransportStop

@Entity(
    tableName = "nc_stop"
)
data class NCStopEntity(
    @PrimaryKey
    @ColumnInfo(name = "stop", index = true)
    val stopId: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "name_tc")
    val nameTc: String,
    @ColumnInfo(name = "name_sc")
    val nameSc: String,
    val lat: String,
    @ColumnInfo(name = "long")
    val lng: String,
) {
    companion object {
        fun fromApiModel(stop: NCStop): NCStopEntity? {
            if (!stop.isValid()) return null

            return NCStopEntity(
                stopId = stop.stopId!!,
                nameEn = stop.nameEn!!,
                nameTc = stop.nameTc!!,
                nameSc = stop.nameSc!!,
                lat = stop.lat!!,
                lng = stop.lng!!
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

    fun toTransportModelWithSeq(seq: String): TransportStop {
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