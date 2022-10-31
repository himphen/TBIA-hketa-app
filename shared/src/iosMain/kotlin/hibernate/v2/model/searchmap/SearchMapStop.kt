package hibernate.v2.model.searchmap

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import hibernate.v2.database.ctb.CtbStopEntity
import hibernate.v2.database.gmb.GmbStopEntity
import hibernate.v2.database.kmb.KmbStopEntity
import hibernate.v2.database.nlb.NlbStopEntity
import hibernate.v2.model.Card
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.utils.TransportationLanguage

actual class SearchMapStop(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    val etaType: EtaType,
    var mapRouteList: List<Card.EtaCard> = emptyList(),
)