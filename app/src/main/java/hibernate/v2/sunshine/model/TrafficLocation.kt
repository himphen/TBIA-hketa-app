package hibernate.v2.sunshine.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.Eta
import hibernate.v2.api.model.Route
import hibernate.v2.api.model.Stop
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrafficLocation(
    @SerializedName("name")
    val name: String,
    @SerializedName("bounds")
    var bounds: LatLngBounds
) : Parcelable