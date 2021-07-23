package hibernate.v2.sunshine.db.traffic.snapshot

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

@Entity(
    tableName = "snapshot_camera"
)
data class CameraEntity(
    @PrimaryKey
    val key: String,
    val region: String,
    val district: String,
    val description: String,
    val easting: String,
    val northing: String,
    val latitude: Double,
    val longitude: Double,
    val url: String
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String {
        return description
    }

    override fun getSnippet(): String {
        return ""
    }

}