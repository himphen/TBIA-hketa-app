package hibernate.v2.sunshine.ui.searchmap

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.ClusterManager
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CustomClusterManager(
    context: Context,
    val map: GoogleMap,
    val lifecycleScope: LifecycleCoroutineScope,
) : ClusterManager<SearchMapStop>(context, map),
    GoogleMap.OnCameraMoveListener {

    val onGetCurrentCircleSize = MutableSharedFlow<Pair<DistanceLevel, Int>>()
    val onCameraMoving = MutableSharedFlow<Boolean>()
    val onRequestStopListForMarker = MutableSharedFlow<Unit>()

    private var isCameraMoving = false
    private val levels = arrayListOf(DistanceLevel.D10, DistanceLevel.D5)

    enum class DistanceLevel(val distance: Double) { D0(0.0), D5(416.7), D10(833.3) }

    companion object {
        const val MARKER_IN_METER = 1250.0
        const val MARKER_IN_ZOOM_LEVEL = 14.5
    }

    var currentZoomLevel: Float? = null

    override fun onCameraMove() {
        if (currentZoomLevel != map.cameraPosition.zoom) {
            currentZoomLevel = map.cameraPosition.zoom

            updateCurrentMarkerCircle()
        }

        if (!isCameraMoving) {
            isCameraMoving = true
            lifecycleScope.launch {
                onCameraMoving.emit(true)
            }
        }
    }

    override fun onCameraIdle() {
        isCameraMoving = false
        lifecycleScope.launch {
            onCameraMoving.emit(false)

            if (currentZoomLevel == null) {
                currentZoomLevel = map.cameraPosition.zoom
            }
            val currentZoomLevel = currentZoomLevel!!
            if (currentZoomLevel >= MARKER_IN_ZOOM_LEVEL) {
                onRequestStopListForMarker.emit(Unit)
            } else {
                removeMarker()
            }
        }
    }

    private fun getDestinationPoint(source: LatLng, brng: Double, dist: Double): LatLng? {
        var brng = brng
        var dist = dist / 1000
        dist /= 6371
        brng = Math.toRadians(brng)
        val lat1 = Math.toRadians(source.latitude)
        val lon1 = Math.toRadians(source.longitude)
        val lat2 = asin(sin(lat1) * cos(dist) + cos(lat1) * sin(dist) * cos(brng))
        val lon2 =
            lon1 + atan2(sin(brng) * sin(dist) * cos(lat1), cos(dist) - sin(lat1) * sin(lat2))
        return if (java.lang.Double.isNaN(lat2) || java.lang.Double.isNaN(lon2)) {
            null
        } else LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }

    private fun getDimensionPointFromLevel(
        projection: Projection,
        currentLatLng: LatLng,
        level: DistanceLevel,
    ): Boolean {
        val topLatLng = getDestinationPoint(currentLatLng, 0.0, level.distance) ?: return false
        val currentPoint = projection.toScreenLocation(currentLatLng)
        val topPoint = projection.toScreenLocation(topLatLng)
        if (topPoint.y < 400) return false
        val diff = currentPoint.y - topPoint.y
//        if (diff < 400) return false

        lifecycleScope.launch {
            onGetCurrentCircleSize.emit(Pair(level, diff))
        }

        return true
    }

    fun updateCurrentMarkerCircle() {
        val projection = map.projection
        val currentLatLng = map.cameraPosition.target

        if (map.cameraPosition.zoom >= MARKER_IN_ZOOM_LEVEL) {
            levels.forEach { if (getDimensionPointFromLevel(projection, currentLatLng, it)) return }
        }

        lifecycleScope.launch {
            onGetCurrentCircleSize.emit(Pair(DistanceLevel.D0, -1))
        }
    }

    fun updateMarker(allStopList: List<SearchMapStop>) {
        val currentPosition = map.cameraPosition.target
        lifecycleScope.launch(Dispatchers.IO) {
            val pendingToAdd = allStopList
                .filter {
                    val distance = SphericalUtil.computeDistanceBetween(
                        it.position, currentPosition
                    )

                    distance < MARKER_IN_METER
                }
            clearItems()
            addItems(pendingToAdd)
            withContext(Dispatchers.Main) {
                cluster()
            }
        }
    }

    private fun removeMarker() {
        lifecycleScope.launch(Dispatchers.Main) {
            clearItems()
            cluster()
        }
    }
}
