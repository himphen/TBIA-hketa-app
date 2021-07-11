package hibernate.v2.api.model.transport.gmb

import com.google.firebase.database.PropertyName

data class GmbStop(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
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
    var stopId: Long = 0
)