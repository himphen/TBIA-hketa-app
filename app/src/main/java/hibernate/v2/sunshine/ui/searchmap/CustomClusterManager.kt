package hibernate.v2.sunshine.ui.searchmap

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomClusterManager(
    context: Context,
    googleMap: GoogleMap,
    val lifecycleScope: LifecycleCoroutineScope
) : ClusterManager<SearchMapStop>(context, googleMap) {

    var isMarkerCollectionHidden = true

    override fun addItems(items: Collection<SearchMapStop>?): Boolean {
        val result = super.addItems(items)
        setMarkerVisibility()
        return result
    }

    override fun addItem(myItem: SearchMapStop?): Boolean {
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