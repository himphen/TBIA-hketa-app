package hibernate.v2.sunshine.model.transport

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportStop(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    var seq: Int? = null
) : Parcelable