package hibernate.v2.sunshine.db.kmb

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import hibernate.v2.api.model.Stop
import hibernate.v2.sunshine.model.TransportStop
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "kmb_stop",
    indices = [Index(value = ["stop"])]
)
data class KmbStopEntity(
    @ColumnInfo(name = "stop")
    @PrimaryKey val stopId: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "name_tc")
    val nameTc: String,
    @ColumnInfo(name = "name_sc")
    val nameSc: String,
    val lat: String,
    val long: String,
) : Parcelable {
    companion object {
        fun fromApiModel(stop: Stop): KmbStopEntity {
            return KmbStopEntity(
                stopId = stop.stopId,
                nameEn = stop.nameEn,
                nameTc = stop.nameTc,
                nameSc = stop.nameSc,
                lat = stop.lat,
                long = stop.long
            )
        }

        fun toTransportModel(stop: KmbStopEntity): TransportStop {
            return TransportStop(
                stopId = stop.stopId,
                nameEn = stop.nameEn,
                nameTc = stop.nameTc,
                nameSc = stop.nameSc,
                lat = stop.lat,
                long = stop.long,
                seq = null,
            )
        }
    }
}