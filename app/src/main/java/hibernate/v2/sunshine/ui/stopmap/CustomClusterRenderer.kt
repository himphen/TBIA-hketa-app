package hibernate.v2.sunshine.ui.stopmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.transport.StopMap

class CustomClusterRenderer(
    val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<StopMap>
) : DefaultClusterRenderer<StopMap>(context, map, clusterManager) {

    init {
//        minClusterSize = 5
    }

    override fun onBeforeClusterItemRendered(item: StopMap, markerOptions: MarkerOptions) {
        val colorRid = item.etaType.etaTypeColors(context)[0]

        val markerDrawable = (ContextCompat.getDrawable(
            context,
            R.drawable.map_marker_bus_stop
        ) as? LayerDrawable)?.apply {
            mutate()
            (findDrawableByLayerId(R.id.stopBackground) as? GradientDrawable)?.setColor(colorRid)
        } ?: return

        val height = markerDrawable.intrinsicHeight
        val width = markerDrawable.intrinsicWidth
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        markerDrawable.setBounds(0, 0, width, height)
        markerDrawable.draw(Canvas(bitmap))

        val descriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
        markerOptions.icon(descriptor)
    }
}