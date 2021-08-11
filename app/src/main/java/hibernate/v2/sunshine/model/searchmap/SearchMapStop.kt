package hibernate.v2.sunshine.model.searchmap

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.TransportStop

data class SearchMapStop(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    val etaType: EtaType,
    var mapRouteList: List<Card.EtaCard> = emptyList(),
) : ClusterItem {
    companion object {
        fun fromStopEntity(it: KmbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.KMB
            )
        }

        fun fromStopEntity(it: NCStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.NWFB
            )
        }

        fun fromStopEntity(it: GmbStopEntity): SearchMapStop {
            return SearchMapStop(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameSc,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.GMB_HKI
            )
        }
    }

    fun toTransportModelWithSeq(seq: Int): TransportStop {
        return TransportStop(
            company = etaType.company(),
            stopId = stopId,
            nameEn = nameEn,
            nameTc = nameTc,
            nameSc = nameSc,
            lat = lat,
            lng = lng,
            seq = seq,
        )
    }

    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }
}