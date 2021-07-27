package hibernate.v2.sunshine.ui.stopmap

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import hibernate.v2.sunshine.model.transport.StopMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomClusterManager(
    context: Context,
    googleMap: GoogleMap,
    val lifecycleScope: LifecycleCoroutineScope
) : ClusterManager<StopMap>(context, googleMap) {

    var isMarkerCollectionHidden = true

    override fun addItems(items: Collection<StopMap>?): Boolean {
        val result = super.addItems(items)
        setMarkerVisibility()
        return result
    }

    override fun addItem(myItem: StopMap?): Boolean {
        val result = super.addItem(myItem)
        setMarkerVisibility()
        return result
    }

    override fun onCameraIdle() {
        setMarkerVisibility()

        super.onCameraIdle()
    }

    fun setMarkerVisibility() {
        if ((renderer as CustomClusterRenderer).currentZoomLevel < 17) {
            if (!isMarkerCollectionHidden) {
                lifecycleScope.launch(Dispatchers.Main) {
                    markerManager.Collection().hideAll()
                }

                isMarkerCollectionHidden = true
            }
        } else {
            if (isMarkerCollectionHidden) {
                lifecycleScope.launch(Dispatchers.Main) {
                    markerManager.Collection().showAll()
                }

                isMarkerCollectionHidden = false
            }
        }
    }
}