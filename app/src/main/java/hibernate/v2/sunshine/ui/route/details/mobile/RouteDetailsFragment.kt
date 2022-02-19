package hibernate.v2.sunshine.ui.route.details.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.himphen.logger.Logger
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentRouteDetailsBinding
import hibernate.v2.sunshine.model.RouteDetailsMarkerItem
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.RouteDetailsStop
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.util.GeneralUtils
import hibernate.v2.sunshine.util.GoogleMapsUtils
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.launchPeriodicAsync
import hibernate.v2.sunshine.util.smoothSnapToPosition
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedStateViewModel

class RouteDetailsFragment : BaseFragment<FragmentRouteDetailsBinding>() {

    companion object {
        fun getInstance() = RouteDetailsFragment()
    }

    private val viewModel: RouteDetailsMobileViewModel by sharedStateViewModel()
    private val adapter: RouteDetailsAdapter by lazy {
        RouteDetailsAdapter(
            viewModel.selectedRoute,
            onItemExpanding = {
                viewModel.selectedStop.value = it.transportStop
                stopRefreshEtaJob()
                startRefreshEtaJob()
            },
            onItemCollapsing = {
                viewModel.selectedStop.value = null
                stopRefreshEtaJob()
            },
            onSaveEtaClicked = { position, card ->
                viewModel.saveStop(position, card)
            }
        )
    }

    private var googleMap: GoogleMap? = null
    private var refreshEtaJob: Deferred<Unit>? = null
    private var etaRequestJob: Job? = null
    private var defaultBounds: LatLngBounds? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentRouteDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            initEvent()
            initUi()
            initData()
        }
    }

    private suspend fun initUi() {
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = adapter
        viewBinding.recyclerView.itemAnimator = null

        val selectedRoute = viewModel.selectedRoute

        (activity as? RouteDetailsActivity)?.let { activity ->
            val color = selectedRoute.getColor(requireContext())
            activity.window?.statusBarColor = color

            activity.supportActionBar?.let {
                it.title = selectedRoute.routeNo
                it.subtitle = selectedRoute.getDirectionSubtitleText(requireContext())
            }

            activity.viewBinding.toolbar.root.setBackgroundColor(color)
        }

        initMap(viewBinding)
    }

    private suspend fun initMap(viewBinding: FragmentRouteDetailsBinding) {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding.map.id) as SupportMapFragment
        googleMap = mapFragment.awaitMap().apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_stop_map)
            )
            isTrafficEnabled = true
            uiSettings.apply {
                isMapToolbarEnabled = false
                isIndoorLevelPickerEnabled = false
                isBuildingsEnabled = false
            }
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }
    }

    private fun initData() {
        viewModel.getRouteDetailsStopList()
    }

    private fun initEvent() {
        viewModel.routeDetailsStopList.observe(viewLifecycleOwner) { routeDetailsStopList ->
            showStopListInMap(routeDetailsStopList)
            adapter.setRouteDetailsStopData(routeDetailsStopList)
        }
        viewModel.selectedStop.observe(viewLifecycleOwner) { selectedStop ->
            selectedStop?.let {
                zoomToStop(selectedStop)

                adapter.expandedPosition.let temp@{ expandedPosition ->
                    if (expandedPosition >= 0) {
                        val layoutManager =
                            (viewBinding?.recyclerView?.layoutManager as? LinearLayoutManager)
                                ?: return@temp

                        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                        if (expandedPosition < firstVisiblePosition || expandedPosition > lastVisiblePosition) {
                            viewBinding?.recyclerView?.scrollToPosition(expandedPosition)
                        } else if (expandedPosition == firstVisiblePosition || expandedPosition == lastVisiblePosition) {
                            viewBinding?.recyclerView?.smoothSnapToPosition(
                                expandedPosition,
                                LinearSmoothScroller.SNAP_TO_ANY
                            )
                        }
                    }
                }
            } ?: run {
                zoomToDefault()
            }
        }
        viewModel.etaList.onEach { etaList ->
            adapter.setEtaData(etaList)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.isSavedEtaBookmark.onEach {
            adapter.setSavedBookmark(it.second)
            if (it.first) {
                activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.etaRequested.onEach {
            if (it) {
                etaRequestJob = viewModel.updateEtaList()
            } else {
                etaRequestJob?.cancel()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.mapMarkerClicked.onEach {
            val selectedStop = viewModel.routeDetailsStopList.value?.getOrNull(it)
            if (selectedStop != null) {
                adapter.normalItemOnClickListener(selectedStop, it)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.etaUpdateError.onEach {
            adapter.setEtaData(emptyList())
            Logger.d("lifecycle etaUpdateError: " + it.message)
            Toast.makeText(
                context,
                getString(R.string.text_eta_loading_failed, 400),
                Toast.LENGTH_LONG
            ).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun startRefreshEtaJob() {
        if (refreshEtaJob == null) {
            Logger.d("lifecycle startRefreshEtaJob")
            refreshEtaJob = lifecycleScope.launchPeriodicAsync(GeneralUtils.REFRESH_TIME) {
                lifecycleScope.launch {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
    }

    private fun stopRefreshEtaJob() {
        Logger.d("lifecycle stopRefreshEtaJob")
        lifecycleScope.launch {
            viewModel.etaRequested.emit(false)
        }
        refreshEtaJob?.cancel()
        refreshEtaJob = null
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun showStopListInMap(routeDetailsStopList: List<RouteDetailsStop>) {
        val googleMap = googleMap ?: return

        val bld = LatLngBounds.Builder()
        routeDetailsStopList.forEachIndexed { index, routeDetailsStop ->
            val latLng = LatLng(
                routeDetailsStop.transportStop.lat,
                routeDetailsStop.transportStop.lng
            )
            bld.include(latLng)

            val markerOptions = MarkerOptions()
                .position(latLng)
                .also { markerOptions ->
                    GoogleMapsUtils.drawMarker(
                        context,
                        when (viewModel.selectedEtaType) {
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
                    )?.let { bitmapDescriptor ->
                        markerOptions.icon(bitmapDescriptor)
                    }
                }

            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = RouteDetailsMarkerItem(index)
        }

        googleMap.setOnMarkerClickListener {
            val tag = it.tag
            if (tag is RouteDetailsMarkerItem) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.mapMarkerClicked.emit(tag.position)
                }
            }

            return@setOnMarkerClickListener true
        }

        defaultBounds = bld.build().also {
            googleMap.setLatLngBoundsForCameraTarget(it)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(it, dpToPx(16)))
        }
    }

    private fun zoomToStop(selectedStop: TransportStop) {
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    selectedStop.lat,
                    selectedStop.lng
                ),
                16f
            )
        )
    }

    private fun zoomToDefault() {
        defaultBounds?.let { defaultBounds ->
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(defaultBounds, dpToPx(16)))
        }
    }

    override fun onResume() {
        super.onResume()
        startRefreshEtaJob()
    }

    override fun onPause() {
        stopRefreshEtaJob()
        super.onPause()
    }
}
