package hibernate.v2.sunshine.ui.searchmap

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.util.GoogleMapsUtils

class CustomClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    val clusterManager: CustomClusterManager,
) : DefaultClusterRenderer<SearchMapStop>(context, map, clusterManager) {

    init {
        minClusterSize = 3
    }

    override fun shouldRenderAsCluster(cluster: Cluster<SearchMapStop>): Boolean {
        clusterManager.currentZoomLevel?.let {
            return super.shouldRenderAsCluster(cluster) && it < 19f
        }

        return super.shouldRenderAsCluster(cluster)
    }

    override fun onBeforeClusterItemRendered(item: SearchMapStop, markerOptions: MarkerOptions) {
        GoogleMapsUtils.drawMarker(
            context,
            when (item.etaType) {
                EtaType.KMB -> R.drawable.map_marker_bus_stop_kmb
                EtaType.NWFB -> R.drawable.map_marker_bus_stop_nc
                EtaType.CTB -> R.drawable.map_marker_bus_stop_ctb
                EtaType.GMB_HKI,
                EtaType.GMB_KLN,
                EtaType.GMB_NT -> R.drawable.map_marker_bus_stop_gmb
                EtaType.MTR -> R.drawable.map_marker_bus_stop_mtr
                EtaType.LRT -> R.drawable.map_marker_bus_stop_lrt
                EtaType.NLB -> R.drawable.map_marker_bus_stop_nlb
            }
        )?.let {
            markerOptions.icon(it)
        }
    }

}