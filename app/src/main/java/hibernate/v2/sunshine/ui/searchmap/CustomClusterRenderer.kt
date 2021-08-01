package hibernate.v2.sunshine.ui.searchmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.EtaType

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
        drawMarker(
            when (item.etaType) {
                EtaType.KMB -> R.drawable.map_marker_bus_stop_kmb
                EtaType.NWFB -> R.drawable.map_marker_bus_stop_nc
                EtaType.CTB -> R.drawable.map_marker_bus_stop_ctb
                EtaType.GMB -> R.drawable.map_marker_bus_stop_gmb
            }
        )?.let {
            markerOptions.icon(it)
        }
    }

    private fun drawMarker(resourceId: Int): BitmapDescriptor? {
        val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}