package hibernate.v2.model.transport

import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import hibernate.v2.MR
import hibernate.v2.api.model.transport.Company
import hibernate.v2.utils.KMMContext
import hibernate.v2.utils.LanguageUtils.isLangEnglish
import hibernate.v2.utils.localized

data class TransportStop(
    val company: Company,
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    var seq: Int? = null
) {

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

    fun getLocalisedName(context: KMMContext): String {
        val localisedName = if (isLangEnglish(context)) {
            nameEn
        } else {
            nameTc
        }

        if (company == Company.MTR || company == Company.LRT) {
            return StringDesc.ResourceFormatted(
                MR.strings.text_eta_card_classic_mtr_station,
                localisedName
            ).localized(context)
        }

        return localisedName
    }
}
