package hibernate.v2.sunshine.model.transport

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class StopMap(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return nameTc
    }

    override fun getSnippet(): String? {
        return nameTc
    }
}