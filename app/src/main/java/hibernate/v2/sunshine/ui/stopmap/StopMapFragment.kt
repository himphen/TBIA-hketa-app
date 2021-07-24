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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentStopMapBinding
import hibernate.v2.sunshine.model.transport.StopMap
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class StopMapFragment : BaseFragment<FragmentStopMapBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentStopMapBinding.inflate(inflater, container, false)

    private lateinit var stopListBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var routeListBottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private var clusterManager: ClusterManager<StopMap>? = null
    private val viewModel: StopMapViewModel by inject()
    private val etaViewModel: AddEtaViewModel by inject()

    private val routeListAdapter = RouteListAdapter(object : RouteListAdapter.ItemListener {
        override fun onRouteSelected(route: TransportRoute) {
            // TODO save eta stop
        }
    })

    private val stopListAdapter = StopListAdapter(object : StopListAdapter.ItemListener {
        override fun onStopSelected(stop: StopMap) {
            // TODO save eta stop
        }
    })

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
            showRouteList(it)
        }
    }

    private suspend fun initUI() {
        val viewBinding = viewBinding!!
        viewBinding.layoutStopList.recyclerView.adapter = stopListAdapter
        viewBinding.layoutRouteList.recyclerView.adapter = routeListAdapter

        initStopListBottomSheet(viewBinding)
        initRouteListBottomSheet(viewBinding)
        initMap(viewBinding)
    }

    private fun initStopListBottomSheet(viewBinding: FragmentStopMapBinding) {
        stopListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutStopList.nestedScrollView).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetCallback() {
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
            }
    }

    private fun initRouteListBottomSheet(viewBinding: FragmentStopMapBinding) {
        routeListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutRouteList.nestedScrollView).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetCallback() {
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
            }
    }

    private suspend fun initMap(viewBinding: FragmentStopMapBinding) {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding.map.id) as SupportMapFragment
        googleMap = mapFragment.awaitMap().apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_stop_map)
            )
            isTrafficEnabled = true
            // set default zoom
            setUpClusterer(this)
        }
    }

    private fun initData() {
        viewModel.getStopList()
    }

    private fun setUpClusterer(googleMap: GoogleMap) {
        context?.let { context ->
            clusterManager = ClusterManager(context, googleMap)
            clusterManager?.apply {
                renderer = CustomClusterRenderer(context, googleMap, this)
                setOnClusterItemClickListener {
                    // single click
                    showRouteBottomSheet(it)
                    false
                }
                setOnClusterClickListener {
                    // group click
                    showStopBottomSheet(it.items.toMutableList())
                    false
                }

                googleMap.setOnCameraIdleListener(clusterManager)
                googleMap.setOnMarkerClickListener(clusterManager)
            }
        }
    }

    private fun showRouteBottomSheet(stopMap: StopMap) {
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutRouteList.stopNameTv.text = stopMap.nameTc
        }
        routeListAdapter.setData(mutableListOf())
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStop(stopMap.etaType, stopMap.stopId)
    }

    private fun showStopBottomSheet(list: List<StopMap>?) {
        if (list == null) return
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutStopList.countTv.text =
                getString(R.string.stop_list_bottom_sheet_title, list.size)
        }

        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        stopListAdapter.setData(list.toMutableList())
    }

    private fun showRouteList(list: List<TransportRoute>?) {
        routeListAdapter.setData(list?.toMutableList())
    }

    private fun showStopListOnMap(list: List<StopMap>?) {
        clusterManager?.addItems(list)

        list?.first()?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 13f))
        }
    }

    fun isBottomSheetShown() =
        routeListBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
                || stopListBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN

    fun closeBottomSheet() {
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
