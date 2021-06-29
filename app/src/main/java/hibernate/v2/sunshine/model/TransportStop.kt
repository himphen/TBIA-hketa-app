package hibernate.v2.sunshine.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportStop(
    val lat: String,
    val long: String,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    var seq: String
) : Parcelable