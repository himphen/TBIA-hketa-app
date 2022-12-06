package hibernate.v2.model.searchmap

import hibernate.v2.database.ctb.CtbStopEntity
import hibernate.v2.database.gmb.GmbStopEntity
import hibernate.v2.database.kmb.KmbStopEntity
import hibernate.v2.database.nlb.NlbStopEntity
import hibernate.v2.model.Card
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.utils.TransportationLanguage

expect class SearchMapStop {
    val lat: Double
    val lng: Double
    val nameEn: String
    val nameSc: String
    val nameTc: String
    val stopId: String
    val etaType: EtaType
    var mapRouteList: List<Card.EtaCard>

    fun getLocalisedName(transportationLanguage: TransportationLanguage): String

    companion object {
        fun fromStopEntity(it: KmbStopEntity): SearchMapStop

        fun fromStopEntity(it: CtbStopEntity): SearchMapStop

        fun fromStopEntity(it: GmbStopEntity): SearchMapStop

        fun fromStopEntity(it: NlbStopEntity): SearchMapStop
    }

    fun toTransportModelWithSeq(seq: Int): TransportStop
}
