package hibernate.v2.tbia.ui.searchmap

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import hibernate.v2.model.searchmap.SearchMapStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.util.GoogleMapsUtils

class CustomClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    private val clusterManager: CustomClusterManager,
) : DefaultClusterRenderer<SearchMapStop>(context, map, clusterManager) {

    private val smallColor: Int
    private val mediumColor: Int
    private val largeColor: Int

    init {
        minClusterSize = 3

        smallColor = context.getColor(R.color.map_clustering_small)
        mediumColor = context.getColor(R.color.map_clustering_medium)
        largeColor = context.getColor(R.color.map_clustering_large)
    }

    override fun getColor(clusterSize: Int): Int {
        return when {
            clusterSize >= 50 -> largeColor
            clusterSize >= 20 -> mediumColor
            else -> smallColor
        }
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
