package hibernate.v2.sunshine.ui.traffic.snapshot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.sunshine.databinding.FragmentTrafficSnapshotBinding
import hibernate.v2.sunshine.db.traffic.snapshot.CameraEntity
import hibernate.v2.sunshine.ui.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SnapshotFragment : BaseFragment<FragmentTrafficSnapshotBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTrafficSnapshotBinding.inflate(inflater, container, false)

    private val viewModel: SnapshotViewModel by inject()

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<CameraEntity>? = null

    companion object {
        private const val REFRESH_TIME = 15L * 1000
        fun getInstance() = SnapshotFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            initUI()
            initData()
        }
    }

    private suspend fun initUI() {
        initMap()
    }

    private suspend fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding!!.map.id) as SupportMapFragment
        googleMap = mapFragment.awaitMap()

        googleMap?.isTrafficEnabled = true
        setUpClusterer()
    }

    private suspend fun initData() {
        withContext(Dispatchers.IO) {
            val list = viewModel.getSnapshotCameraList()

            list.forEach {
                clusterManager?.addItem(it)
            }
        }
    }

    private fun setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        context?.let { context ->
            clusterManager = ClusterManager(context, googleMap)

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            googleMap?.setOnCameraIdleListener(clusterManager)
            googleMap?.setOnMarkerClickListener(clusterManager)
        }
    }
}