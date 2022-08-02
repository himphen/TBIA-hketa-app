package hibernate.v2.sunshine.model.transport

import android.content.Context
import android.os.Parcelable
import hibernate.v2.api.model.transport.Company
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.util.GeneralUtils.isLangEnglish
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

    fun getLocalisedName(context: Context): String {
        val localisedName = if (isLangEnglish(context)) {
            nameEn
        } else {
            nameTc
        }

        if (company == Company.MTR || company == Company.LRT) {
            return context.getString(R.string.text_eta_card_classic_mtr_station, localisedName)
        }

        return localisedName
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
