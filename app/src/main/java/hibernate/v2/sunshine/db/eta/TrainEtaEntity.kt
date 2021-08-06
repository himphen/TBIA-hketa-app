package hibernate.v2.sunshine.db.eta

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hibernate.v2.api.model.transport.Company
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "saved_train_eta")
data class TrainEtaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "saved_train_eta_id")
    val id: Long? = null,
    @ColumnInfo(name = "saved_train_eta_company")
    val company: Company,
    @ColumnInfo(name = "saved_train_eta_stop_id")
    val stopId: String
) : Parcelable