package hibernate.v2.sunshine.model

import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.serialization.SerialName

data class TrafficLocation(
    @SerialName("name")
    val name: String,
    @SerialName("bounds")
    var bounds: LatLngBounds,
)
