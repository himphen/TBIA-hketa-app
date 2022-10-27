package hibernate.v2.model.transport

import hibernate.v2.api.model.transport.Company

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
}
