package hibernate.v2.sunshine.model

import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.annotations.SerializedName

data class TrafficLocation(
    @SerializedName("name")
    val name: String,
    @SerializedName("bounds")
    var bounds: LatLngBounds,
)
