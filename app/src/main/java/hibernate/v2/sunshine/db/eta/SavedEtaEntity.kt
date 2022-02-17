package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.model.transport.TransportHashable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "saved_eta")
data class SavedEtaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "saved_eta_id")
    val id: Long? = null,
    @ColumnInfo(name = "saved_eta_company")
    val company: Company,
    @ColumnInfo(name = "saved_eta_stop_id")
    val stopId: String,
    @ColumnInfo(name = "saved_eta_route_id")
    val routeId: String,
    @ColumnInfo(name = "saved_eta_route_bound")
    val bound: Bound,
    @ColumnInfo(name = "saved_eta_service_type")
    val serviceType: String,
    @ColumnInfo(name = "saved_eta_seq")
    val seq: Int,
) : Parcelable, TransportHashable {

    fun routeHashId() = routeHashId(company, routeId, bound, serviceType)
    fun stopHashId() = stopHashId(routeId, bound, serviceType, stopId, seq)
}
