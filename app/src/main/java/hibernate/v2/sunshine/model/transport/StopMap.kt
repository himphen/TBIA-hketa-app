package hibernate.v2.sunshine.model.transport

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity

class StopMap(
    val lat: Double,
    val lng: Double,
    val nameEn: String,
    val nameSc: String,
    val nameTc: String,
    val stopId: String,
    val etaType: EtaType
) : ClusterItem {
    companion object {
        fun fromStopEntity(it: KmbStopEntity): StopMap {
            return StopMap(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameEn,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.KMB
            )
        }

        fun fromStopEntity(it: NCStopEntity): StopMap {
            return StopMap(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameEn,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.NWFB_CTB
            )
        }

        fun fromStopEntity(it: GmbStopEntity): StopMap {
            return StopMap(
                lat = it.lat,
                lng = it.lng,
                nameEn = it.nameEn,
                nameSc = it.nameEn,
                nameTc = it.nameTc,
                stopId = it.stopId,
                etaType = EtaType.GMB
            )
        }
    }

    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return nameTc
    }

    override fun getSnippet(): String? {
        return nameTc
    }
}