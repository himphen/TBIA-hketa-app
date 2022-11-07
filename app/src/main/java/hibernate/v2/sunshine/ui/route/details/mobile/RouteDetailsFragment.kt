package hibernate.v2.sunshine.ui.route.details.mobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.api.model.transport.Company
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.model.transport.RouteDetailsStop
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentRouteDetailsBinding
import hibernate.v2.sunshine.model.RouteDetailsMarkerItem
import hibernate.v2.sunshine.model.color
import hibernate.v2.sunshine.model.getColor
import hibernate.v2.sunshine.model.getDirectionSubtitleText
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.util.GeneralUtils
import hibernate.v2.sunshine.util.GoogleMapsUtils
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.launchPeriodicAsync
import hibernate.v2.sunshine.util.smoothSnapToPosition
import hibernate.v2.sunshine.util.visible
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedStateViewModel

class RouteDetailsFragment : BaseFragment<FragmentRouteDetailsBinding>() {

    companion object {
        fun getInstance() = RouteDetailsFragment()
    }

    private val preferences by inject<SharedPreferencesManager>()
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
            onAddButtonClicked = { position, card ->
                viewModel.saveBookmark(position, card)
            },
            onRemoveButtonClicked = { position, entityId ->
                viewModel.removeBookmark(position, entityId)
            },
            onNavigationButtonClicked = {
                goToNavigationActivity(it)
            },
            onStreetViewButtonClicked = {
                goToStreetMapsActivity(it)
            }
        )
    }

    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var refreshEtaJob: Deferred<Unit>? = null
    private var etaRequestJob: Job? = null
    private var defaultBounds: LatLngBounds? = null

    private val locationPermissionRequest =
        locationPermissionRequest { getCurrentLocationInMap(false) }
    private val locationRequest = LocationRequest.create().apply {
        fastestInterval = 5000L
        interval = 10000L
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            logLifecycle("locationCallback")
            zoomToNearestStop(locationResult.lastLocation)

            stopRequestLocation()
        }
    }

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

        viewBinding.recyclerView.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int) =
                EdgeEffect(view.context).apply {
                    color = viewModel.selectedEtaType.color(context!!)
                }
        }

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

        viewBinding.trafficLayerBtn.setOnClickListener {
            val newValue = !preferences.trafficLayerToggle
            preferences.trafficLayerToggle = newValue
            toggleTrafficLayerInMap(newValue)
        }

        viewBinding.myLocationBtn.setOnClickListener {
            getCurrentLocationInMap(false)
        }

        initMap(viewBinding)
    }

    private suspend fun initMap(viewBinding: FragmentRouteDetailsBinding) {
        when (viewModel.selectedRoute.company) {
            Company.LRT,
            Company.MTR -> {
                viewBinding.mapContainer.gone()
                return
            }
            else -> {
                viewBinding.mapContainer.visible()
            }
        }

        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding.map.id) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        googleMap = mapFragment.awaitMap().apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_stop_map)
            )
            isTrafficEnabled = preferences.trafficLayerToggle
            uiSettings.apply {
                isMapToolbarEnabled = false
                isIndoorLevelPickerEnabled = false
                isBuildingsEnabled = false
            }
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))

            getCurrentLocationInMap(true)

            // If moved camera, stop location request
            setOnCameraMoveStartedListener {
                if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    stopRequestLocation()
                }
            }
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
            adapter.setSavedBookmark(it.first, it.second)
            if (it.second > 0) {
                activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_added),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_removed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isRemovedEtaBookmark.onEach {
            adapter.setRemovedBookmark(it)
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
            logLifecycle("etaUpdateError: " + it.message)
            Toast.makeText(
                context,
                getString(R.string.text_eta_loading_failed, 400),
                Toast.LENGTH_LONG
            ).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.onChangedTrafficLayerToggle.onEach {
            googleMap?.isTrafficEnabled = it
            viewBinding?.trafficLayerBtn?.isSelected = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.requestedLocationUpdates.observe(viewLifecycleOwner) {
            viewBinding?.myLocationBtn?.apply {
                if (it) {
                    setImageDrawable(context.getDrawable(R.drawable.ic_location_24))
                }
            }
        }
    }

    private fun startRefreshEtaJob() {
        if (refreshEtaJob == null) {
            logLifecycle("startRefreshEtaJob")
            refreshEtaJob = lifecycleScope.launchPeriodicAsync(GeneralUtils.ETA_REFRESH_TIME) {
                lifecycleScope.launch {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
    }

    private fun stopRefreshEtaJob() {
        logLifecycle("stopRefreshEtaJob")
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

        // prevent no included points
        if (routeDetailsStopList.isEmpty()) return

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
        stopRequestLocation()
        super.onPause()
    }

    private fun stopRequestLocation() {
        if (viewModel.requestedLocationUpdates.value == true) {
            fusedLocationClient?.removeLocationUpdates(locationCallback)

            viewModel.requestedLocationUpdates.postValue(false)
        }
    }

    private fun toggleTrafficLayerInMap(value: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.onChangedTrafficLayerToggle.emit(value)
        }
    }

    private fun getCurrentLocationInMap(isTry: Boolean) {
        logLifecycle("getCurrentLocationInMap")
        val activity = activity ?: return

        val fineLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermissionGranted && !coarseLocationPermissionGranted) {
            if (isTry) return

            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        logLifecycle("getCurrentLocationInMap has permission")
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        viewModel.requestedLocationUpdates.postValue(true)
    }

    private fun zoomToNearestStop(location: Location?) {
        logLifecycle("zoomToMyLocation location: $location")
        if (location == null) return

        var shortestDistance = 500.0
        var nearestRouteDetailsStop = viewModel.routeDetailsStopList.value?.getOrNull(0) ?: return
        var index = -1

        viewModel.routeDetailsStopList.value?.forEachIndexed { _index, it ->
            val targetDistance = SphericalUtil.computeDistanceBetween(
                LatLng(location.latitude, location.longitude),
                LatLng(it.transportStop.lat, it.transportStop.lng)
            )
            if (targetDistance < shortestDistance) {
                shortestDistance = targetDistance
                nearestRouteDetailsStop = it
                index = _index
            }
        }

        if (shortestDistance <= 500 && index >= 0) {
            viewBinding?.recyclerView?.scrollToPosition(index)
            adapter.normalItemOnClickListener(nearestRouteDetailsStop, index)

            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        nearestRouteDetailsStop.transportStop.lat,
                        nearestRouteDetailsStop.transportStop.lng
                    ),
                    15f
                ),
                500,
                null
            )
        }
    }

    private fun goToStreetMapsActivity(stop: RouteDetailsStop) {
        activity?.let { activity ->
            val gmmIntentUri =
                Uri.parse("google.streetview:cbll=${stop.transportStop.lat},${stop.transportStop.lng}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity.packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun goToNavigationActivity(stop: RouteDetailsStop) {
        activity?.let { activity ->
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${stop.transportStop.lat},${stop.transportStop.lng}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity.packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }
}
