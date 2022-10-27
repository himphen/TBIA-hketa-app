package hibernate.v2.database.kmb

import hibernatev2database.Kmb_stop

data class KmbStopEntityNew(
    val stopId: String,
    val nameEn: String,
    val nameTc: String,
    val nameSc: String,
    val lat: Double,
    val lng: Double,
    val geohash: String,
) {
    companion object {
        fun convertFrom(item: Kmb_stop): KmbStopEntityNew {
            return KmbStopEntityNew(
                stopId = item.kmb_stop_id,
                nameEn = item.name_en,
                nameTc = item.name_tc,
                nameSc = item.name_sc,
                lat = item.lat,
                lng = item.lng,
                geohash = item.geohash,
            )
        }
    }
}
