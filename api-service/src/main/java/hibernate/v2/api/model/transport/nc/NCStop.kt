package hibernate.v2.api.model.transport.nc

import com.google.firebase.database.PropertyName

data class NCStop(
    /**
     * Document shows this is double type
     * However, API return string instead of double
     */
    @get:PropertyName("lat")
    @set:PropertyName("lat")
    var lat: String = "",
    @get:PropertyName("long")
    @set:PropertyName("long")
    var lng: String = "",
    @get:PropertyName("name_en")
    @set:PropertyName("name_en")
    var nameEn: String = "",
    @get:PropertyName("name_sc")
    @set:PropertyName("name_sc")
    var nameSc: String = "",
    @get:PropertyName("name_tc")
    @set:PropertyName("name_tc")
    var nameTc: String = "",
    @get:PropertyName("stop")
    @set:PropertyName("stop")
    var stopId: String = "",
    @get:PropertyName("geohash")
    @set:PropertyName("geohash")
    var geohash: String = "",
)
