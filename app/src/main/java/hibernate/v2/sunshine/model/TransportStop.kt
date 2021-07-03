package hibernate.v2.sunshine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportStop(
    val lat: String,
    val lng: String,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    var seq: String? = null
) : Parcelable