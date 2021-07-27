package hibernate.v2.sunshine.ui.stopmap

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
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

    private lateinit var stopListBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var routeListBottomSheetBehavior: BottomSheetBehavior<*>

    private var clusterManager: CustomClusterManager? = null
    private val viewModel: StopMapViewModel by inject()
    private val etaViewModel: AddEtaViewModel by inject()

    private val routeListAdapter = RouteListAdapter(object : RouteListAdapter.ItemListener {
        override fun onRouteSelected(route: TransportRoute) {
            // TODO save eta stop
        }
    })

    private val stopListAdapter = StopListAdapter(object : StopListAdapter.ItemListener {
        override fun onStopSelected(stop: StopMap) {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(stop.position, 19f))
            showRouteBottomSheet(stopMap = stop)
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

        viewModel.stopListForBottomSheet.observe(viewLifecycleOwner) {
            showStopListOnBottomSheet(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private suspend fun initUI() {
        val viewBinding = viewBinding!!
        val dividerItemDecoration =
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        viewBinding.layoutStopList.recyclerView.apply {
            adapter = stopListAdapter
            addItemDecoration(dividerItemDecoration)
        }

        viewBinding.layoutRouteList.recyclerView.apply {
            adapter = routeListAdapter
            addItemDecoration(dividerItemDecoration)
        }

        initStopListBottomSheet(viewBinding)
        initRouteListBottomSheet(viewBinding)
        initMap(viewBinding)
    }

    private fun initStopListBottomSheet(viewBinding: FragmentStopMapBinding) {
        stopListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutStopList.root).apply {
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
            BottomSheetBehavior.from(viewBinding.layoutRouteList.root).apply {
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
//            isTrafficEnabled = true
            setMinZoomPreference(10f)
            setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    LatLng(22.169436, 113.747226),
                    LatLng(22.555006, 114.418655)
                )
            )

            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.3111191, 114.1688018), 15f))
            // set default zoom
            setUpClusterer(this)
        }
    }

    private fun initData() {
        viewModel.getStopList()
    }

    private fun setUpClusterer(googleMap: GoogleMap) {
        context?.let { context ->
            clusterManager = CustomClusterManager(
                context,
                googleMap,
                lifecycleScope
            )

            clusterManager?.apply {
                val renderer = CustomClusterRenderer(context, googleMap, this)
                this.renderer = renderer
                setOnClusterItemClickListener {
                    // single click
                    showRouteBottomSheet(it)
                    false
                }
                setOnClusterClickListener {
                    // group click

                    if (it.items.size > 30
                        && googleMap.cameraPosition.zoom < googleMap.maxZoomLevel - 1f
                    ) {
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                it.position,
                                googleMap.cameraPosition.zoom + 1f
                            ),
                            500,
                            null
                        )
                    } else {
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLng(
                                it.position
                            )
                        )
                        showStopBottomSheet(it.items.toMutableList())
                    }
                    true
                }

                googleMap.setOnCameraIdleListener(clusterManager)
                googleMap.setOnCameraMoveListener(renderer)
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
        viewModel.getRouteListFromStopList(list)
    }

    private fun showStopListOnBottomSheet(list: List<StopMap>?) {
        stopListAdapter.setData(list?.toMutableList())
    }

    private fun showRouteList(list: List<TransportRoute>?) {
        routeListAdapter.setData(list?.toMutableList())
    }

    private fun showStopListOnMap(list: List<StopMap>?) {
        clusterManager?.addItems(list)
    }

    fun isBottomSheetShown() =
        routeListBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
                || stopListBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN

    fun closeBottomSheet() {
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
