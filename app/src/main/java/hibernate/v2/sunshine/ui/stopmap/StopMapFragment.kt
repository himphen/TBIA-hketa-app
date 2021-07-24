package hibernate.v2.sunshine.ui.stopmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.sunshine.databinding.FragmentStopMapBinding
import hibernate.v2.sunshine.model.transport.StopMap
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.ui.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class StopMapFragment : BaseFragment<FragmentStopMapBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentStopMapBinding.inflate(inflater, container, false)

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private var clusterManager: ClusterManager<StopMap>? = null
    private val viewModel: StopMapViewModel by inject()

    private val stopMapAdapter = StopMapAdapter()

    private var googleMap: GoogleMap? = null

    companion object {
        fun getInstance() = StopMapFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            initEvent()
            initUI()
            initData()
        }
    }

    private fun initEvent() {
        viewModel.stopList.observe(viewLifecycleOwner) {
            showStopListOnMap(it)
        }

        viewModel.routeListForBottomSheet.observe(viewLifecycleOwner) {
            showRouteListInBottomSheet(it)
        }
    }

    private suspend fun initUI() {
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = stopMapAdapter

        initBottomSheet()
        initMap(viewBinding)
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(viewBinding!!.nestedScrollView)
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.4f
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED,
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED,
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        bottomSheetBehavior.peekHeight = 100

    }

    private suspend fun initMap(viewBinding: FragmentStopMapBinding) {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding.map.id) as SupportMapFragment
        googleMap = mapFragment.awaitMap()

        googleMap?.isTrafficEnabled = true
        setUpClusterer()
    }

    private fun initData() {
        viewModel.getStopList()
    }

    private fun setUpClusterer() {
        context?.let { context ->
            clusterManager = ClusterManager(context, googleMap)
            clusterManager?.apply {
                setOnClusterItemClickListener {
                    showStopInBottomSheet(it)
                    true
                }
            }

            // Point the map's listeners at the listeners implemented by the cluster
            // manager.
            googleMap?.setOnCameraIdleListener(clusterManager)
            googleMap?.setOnMarkerClickListener(clusterManager)
        }
    }

    private fun showStopInBottomSheet(stopMap: StopMap) {
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        // TODO bottomSheetDialog.showLoading()
        viewModel.getRouteListFromStop(stopMap.stopId)
    }

    private fun showRouteListInBottomSheet(list: List<TransportRoute>?) {
        stopMapAdapter.setData(list?.toMutableList())
        // TODO bottomSheetDialog.hideLoading()
    }

    private fun showStopListOnMap(list: List<StopMap>?) {
        clusterManager?.addItems(list)

        list?.first()?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 13f))
        }
    }

    fun isBottomSheetShown() = bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
    fun closeBottomSheet() {
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
