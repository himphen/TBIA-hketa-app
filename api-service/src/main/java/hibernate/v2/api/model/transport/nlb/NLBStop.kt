package hibernate.v2.api.model.transport.nlb

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class NLBStop(
    @get:PropertyName("lat")
    @set:PropertyName("lat")
    var lat: String = "0.0",
    @get:PropertyName("long")
    @set:PropertyName("long")
    var lng: String = "0.0",
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
