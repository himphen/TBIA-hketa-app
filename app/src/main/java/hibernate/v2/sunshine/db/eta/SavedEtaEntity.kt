package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.RouteHashable
import hibernate.v2.sunshine.model.transport.StopHashable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "saved_eta")
data class SavedEtaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val company: Company,
    @ColumnInfo(name = "stop")
    val stopId: String,
    @ColumnInfo(name = "route")
    val routeId: String,
    val bound: Bound,
    @ColumnInfo(name = "service_type")
    val serviceType: String,
    val seq: Int,
) : Parcelable, RouteHashable, StopHashable {

    override fun routeHashId() = company.value + routeId + bound.value + serviceType

    override fun stopHashId() = routeId + bound.value + serviceType + stopId + seq
}