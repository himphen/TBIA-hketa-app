package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportStop(
    val company: Company,
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    var seq: Int? = null
) : Parcelable {

    fun getName(context: Context): String {
        if (company == Company.MTR || company == Company.LRT) {
            return context.getString(R.string.text_eta_card_classic_mtr_station, nameTc)
        }

        return nameTc
    }

    companion object {
        fun notFoundStop() = TransportStop(
            company = Company.UNKNOWN,
            lat = 0.0,
            lng = 0.0,
            nameEn = "-", // TODO
            nameSc = "-", // TODO
            nameTc = "-",
            stopId = "",
        )
    }
}