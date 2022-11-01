package hibernate.v2.sunshine.ui.searchmap

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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.ktx.awaitMap
import com.himphen.logger.Logger
import hibernate.v2.geohash.GeoHash
import hibernate.v2.model.Card
import hibernate.v2.model.searchmap.SearchMapStop
import hibernate.v2.model.transport.eta.TransportEta
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentSearchMapBinding
import hibernate.v2.sunshine.model.getLocalisedName
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.bookmark.BookmarkSaveViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.util.GeneralUtils
import hibernate.v2.sunshine.util.GeneralUtils.ETA_LAST_UPDATED_REFRESH_TIME
import hibernate.v2.sunshine.util.GeneralUtils.getTransportationLanguage
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.launchPeriodicAsync
import hibernate.v2.sunshine.util.tickerFlow
import hibernate.v2.sunshine.util.visible
import hibernate.v2.utils.getTimeDiffFromNowInMin
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

class SearchMapFragment : BaseFragment<FragmentSearchMapBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSearchMapBinding.inflate(inflater, container, false)

    private lateinit var stopListBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var routeListBottomSheetBehavior: BottomSheetBehavior<*>

    private val preferences: SharedPreferencesManager by inject()
    private val viewModel: SearchMapViewModel by inject()
    private val bookmarkSaveViewModel: BookmarkSaveViewModel by inject()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private var clusterManager: CustomClusterManager? = null

    private var refreshEtaJob: Deferred<Unit>? = null
    private var etaRequestJob: Job? = null

    private val routeListAdapter = RouteListAdapter { card: Card.EtaCard ->
        viewLifecycleOwner.lifecycleScope.launch {
            val context = context ?: return@launch
            val result = suspendCancellableCoroutine<Boolean> { cont ->
                MaterialAlertDialogBuilder(context)
                    .setMessage(
                        getString(
                            R.string.dialog_add_eta_in_search_map_description_bus,
                            card.route.routeNo,
                            card.stop.getLocalisedName(context)
                        )
                    )
                    .setPositiveButton(R.string.dialog_add_eta_in_search_map_confirm_btn) { _, _ ->
                        cont.resume(true)
                    }
                    .setNegativeButton(R.string.dialog_add_eta_in_search_map_cancel_btn) { _, _ ->
                        cont.resume(false)
                    }
                    .setOnCancelListener {
                        cont.resume(false)
                    }
                    .show()
            }

            if (result) {
                val addCard = Card.RouteStopAddCard(
                    card.route,
                    card.stop
                )
                bookmarkSaveViewModel.saveStop(addCard)
            }
        }
    }

    private val stopListAdapter = StopListAdapter { searchMapStop: SearchMapStop ->
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(searchMapStop.position, 19f))
        showRouteBottomSheet(stop = searchMapStop)
    }

    private var googleMap: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val locationPermissionRequest = locationPermissionRequest { getCurrentLocationInMap() }
    private val locationRequest = LocationRequest.create().apply {
        fastestInterval = 5000L
        interval = 10000L
        priority = PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            logLifecycle("locationCallback")
            zoomToMyLocation(locationResult.lastLocation)
        }
    }

    companion object {
        fun getInstance() = SearchMapFragment()
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
        viewModel.startRefreshEtaJob.onEach {
            logLifecycle("startRefreshEtaJob onEach")
            startRefreshEtaJob()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stopList.onEach {
            showStopListOnMap(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.routeListForBottomSheet.observe(viewLifecycleOwner) {
            viewModel.lastUpdatedTime.postValue(System.currentTimeMillis())
            showRouteListOnBottomSheet(it)
        }

        viewModel.stopListForBottomSheet.observe(viewLifecycleOwner) {
            showStopListOnBottomSheet(it)
        }

        bookmarkSaveViewModel.isAddEtaSuccessful.onEach {
            if (it) {
                lifecycleScope.launchWhenResumed {
                    mainViewModel.onUpdatedEtaList.emit(Unit)
                }
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_added),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_already_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        mainViewModel.onRequestedCloseBottomSheet.onEach {
            closeBottomSheet()
        }.launchIn(lifecycleScope)

        mainViewModel.onRouteBottomSheetStateChanged.observe(viewLifecycleOwner) {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                viewModel.lastUpdatedTime.postValue(-1)
                stopRefreshEtaJob()
                routeListAdapter.setData(mutableListOf())
            }
        }

        mainViewModel.onStopBottomSheetStateChanged.observe(viewLifecycleOwner) {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                stopListAdapter.submitList(null)
            }
        }

        mainViewModel.onChangedTrafficLayerToggle.onEach {
            googleMap?.isTrafficEnabled = it
            viewBinding?.trafficLayerBtn?.isSelected = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.requestedLocationUpdates.observe(viewLifecycleOwner) {
            viewBinding?.myLocationBtn?.apply {
                if (it) {
                    setImageDrawable(context.getDrawable(R.drawable.ic_location_24))
                } else {
                    setImageDrawable(context.getDrawable(R.drawable.ic_location_searching_24))
                }
//                isSelected = it
            }
        }

        viewModel.etaUpdateError.onEach {
            Logger.e(it, "etaUpdateError")
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.etaRequested.onEach {
            if (it) {
                etaRequestJob = viewModel.updateEtaList()
            } else {
                etaRequestJob?.cancel()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                logLifecycle("repeatOnLifecycle viewModel.lastUpdatedTime")
                tickerFlow(ETA_LAST_UPDATED_REFRESH_TIME.milliseconds).collect {
                    viewModel.lastUpdatedTime.value?.let { lastUpdatedTime ->
                        if (lastUpdatedTime > 0) {
                            viewBinding?.layoutRouteList?.lastUpdatedTv?.text = getString(
                                R.string.eta_last_updated_at,
                                ((System.currentTimeMillis() - lastUpdatedTime) / 1000).toInt()
                            )
                        } else {
                            viewBinding?.layoutRouteList?.lastUpdatedTv?.text = getString(
                                R.string.eta_last_updated_at_init
                            )
                            viewModel.lastUpdatedTime.postValue(null)
                        }
                    }
                }
            }
        }
    }

    private fun checkMapToggleButton() {
        if (googleMap == null) return

        val value = preferences.trafficLayerToggle

        toggleTrafficLayerInMap(value)
    }

    private fun getCurrentLocationInMap() {
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
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        logLifecycle("getCurrentLocationInMap has permission")

        fusedLocationClient?.lastLocation?.addOnSuccessListener(activity) {
            logLifecycle("lastLocation?.addOnSuccessListener")
            zoomToMyLocation(it)
        }

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        viewModel.requestedLocationUpdates.postValue(true)
    }

    private fun toggleTrafficLayerInMap(value: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.onChangedTrafficLayerToggle.emit(value)
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
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        viewBinding.layoutRouteList.recyclerView.apply {
            adapter = routeListAdapter
            addItemDecoration(dividerItemDecoration)
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        viewBinding.layoutRouteList.streetViewBtn.setOnClickListener {
            viewModel.selectedStop.value?.let {
                goToStreetMapsActivity(it)
            }
        }

        viewBinding.layoutRouteList.navigationBtn.setOnClickListener {
            viewModel.selectedStop.value?.let {
                goToNavigationActivity(it)
            }
        }

        viewBinding.trafficLayerBtn.setOnClickListener {
            val newValue = !preferences.trafficLayerToggle
            preferences.trafficLayerToggle = newValue
            toggleTrafficLayerInMap(newValue)
        }

        viewBinding.myLocationBtn.setOnClickListener {
            if (viewModel.requestedLocationUpdates.value == true) {
                viewModel.requestedLocationUpdates.postValue(false)
                stopRequestLocation()
            } else {
                getCurrentLocationInMap()
            }
        }

        initStopListBottomSheet(viewBinding)
        initRouteListBottomSheet(viewBinding)
        initMap(viewBinding)
    }

    private fun initStopListBottomSheet(viewBinding: FragmentSearchMapBinding) {
        stopListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutStopList.root).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        Logger.d("onStateChanged stopListBottomSheetBehavior newState: $newState")
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN,
                            BottomSheetBehavior.STATE_HALF_EXPANDED,
                            BottomSheetBehavior.STATE_COLLAPSED,
                            BottomSheetBehavior.STATE_EXPANDED,
                            -> {
                                mainViewModel.onStopBottomSheetStateChanged.value = newState
                            }
                            else -> {
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
    }

    private fun initRouteListBottomSheet(viewBinding: FragmentSearchMapBinding) {
        routeListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutRouteList.root).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        Logger.d("onStateChanged routeListBottomSheetBehavior newState: $newState")
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN,
                            BottomSheetBehavior.STATE_HALF_EXPANDED,
                            BottomSheetBehavior.STATE_COLLAPSED,
                            BottomSheetBehavior.STATE_EXPANDED,
                            -> {
                                mainViewModel.onRouteBottomSheetStateChanged.value = newState
                            }
                            else -> {
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                })
            }
    }

    private suspend fun initMap(viewBinding: FragmentSearchMapBinding) {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding.map.id) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        googleMap = mapFragment.awaitMap().apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_stop_map)
            )
            uiSettings.apply {
                isMapToolbarEnabled = false
                isIndoorLevelPickerEnabled = false
                isBuildingsEnabled = false
            }
            setMinZoomPreference(10f)
            setLatLngBoundsForCameraTarget(
                LatLngBounds(
                    LatLng(22.169436, 113.747226),
                    LatLng(22.555006, 114.418655)
                )
            )
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(116))
            moveCamera(CameraUpdateFactory.newLatLngZoom(preferences.lastLatLng, 15f))
            // set default zoom
            setUpClusterer(this)

            checkMapToggleButton()

            // If moved camera, stop location request
            setOnCameraMoveStartedListener {
                if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    disableRequestLocationUpdates()
                }
            }
        }
    }

    private fun initData() {
    }

    private fun setUpClusterer(googleMap: GoogleMap) {
        context?.let { context ->
            clusterManager = CustomClusterManager(
                context,
                googleMap,
                viewLifecycleOwner.lifecycleScope
            )
            clusterManager?.apply {
                onGetCurrentCircleSize.onEach {
                    viewBinding?.currentMarkerCircle?.apply {
                        if (it.second < 20) {
                            gone()
                        } else {
                            visible()
                            val pixel = dpToPx(it.second)
                            val newLayoutParams = layoutParams as ConstraintLayout.LayoutParams
                            newLayoutParams.width = pixel
                            newLayoutParams.height = pixel
                            requestLayout()
                        }
                    }

                    viewBinding?.currentMarkerText?.apply {
                        text = when (it.first) {
                            CustomClusterManager.DistanceLevel.D0 -> ""
                            CustomClusterManager.DistanceLevel.D5 -> getString(R.string.map_marker_distance_5)
                            CustomClusterManager.DistanceLevel.D10 -> getString(R.string.map_marker_distance_10)
                        }
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
                onCameraMoving.onEach {
                    if (it) {
                        viewBinding?.currentMarkerText?.apply {
                            setTextColor(context.getColor(R.color.map_marker_moving))
                        }
                        viewBinding?.currentMarkerCircle?.setBackgroundResource(R.drawable.map_marker_current_dashed_moving)
                        viewBinding?.currentMarker?.setBackgroundResource(R.drawable.map_marker_current_moving)
                    } else {
                        // Save last position for next launch
                        preferences.lastLatLng = map.cameraPosition.target

                        viewBinding?.currentMarkerText?.apply {
                            setTextColor(context.getColor(R.color.map_marker))
                        }
                        viewBinding?.currentMarkerCircle?.setBackgroundResource(R.drawable.map_marker_current_dashed)
                        viewBinding?.currentMarker?.setBackgroundResource(R.drawable.map_marker_current)
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)
                onRequestStopListForMarker.onEach {
                    map.cameraPosition.target.run {
                        val currentPositionGeoHash = GeoHash(latitude, longitude, 6)
                        val list = currentPositionGeoHash.adjacent.toMutableList()
                            .apply { add(currentPositionGeoHash) }
                        viewModel.getStopList(list)
                    }
                }.launchIn(viewLifecycleOwner.lifecycleScope)

                val renderer = CustomClusterRenderer(context, googleMap, this)
                this.renderer = renderer
                setOnClusterItemClickListener {
                    // single click
                    disableRequestLocationUpdates()
                    showRouteBottomSheet(it)
                    false
                }
                setOnClusterClickListener {
                    // group click
                    disableRequestLocationUpdates()

                    if (it.items.size > 30 &&
                        googleMap.cameraPosition.zoom < googleMap.maxZoomLevel - 1f
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
                googleMap.setOnCameraMoveListener(clusterManager)

                updateCurrentMarkerCircle()
            }
        }
    }

    private fun showRouteBottomSheet(stop: SearchMapStop) {
        viewModel.selectedStop.value = stop
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutRouteList.stopNameTv.text =
                stop.getLocalisedName(getTransportationLanguage(it.root.context))
        }
        routeListAdapter.setData(mutableListOf())
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStop()
    }

    private fun showStopBottomSheet(list: List<SearchMapStop>?) {
        if (list == null) return
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        stopListAdapter.submitList(null)
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStopList(list)
    }

    private fun showStopListOnBottomSheet(list: List<SearchMapStop>?) {
        logLifecycle("showStopListOnBottomSheet")
        stopListAdapter.submitList(null)
        stopListAdapter.submitList(list)
    }

    private fun showRouteListOnBottomSheet(list: List<Card.EtaCard>) {
        logLifecycle("showRouteListOnBottomSheet")
        val temp = list.map { etaCard ->
            val temp = etaCard.etaList.filter { eta: TransportEta ->
                eta.eta?.let { etaDate ->
                    getTimeDiffFromNowInMin(etaDate) > 0
                } ?: run {
                    false
                }
            }

            etaCard.etaList.clear()
            etaCard.etaList.addAll(temp)

            etaCard
        }

        routeListAdapter.setData(temp.toMutableList())
    }

    private fun showStopListOnMap(list: List<SearchMapStop>) {
        clusterManager?.apply {
            updateMarker(list)
        }
    }

    private fun closeBottomSheet() {
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onResume() {
        super.onResume()
        startRefreshEtaJob()
        checkMapToggleButton()

        if (viewModel.requestedLocationUpdates.value == true) {
            getCurrentLocationInMap()
        }
    }

    override fun onPause() {
        stopRefreshEtaJob()
        stopRequestLocation()
        super.onPause()
    }

    private fun disableRequestLocationUpdates() {
        if (viewModel.requestedLocationUpdates.value == true) {
            viewModel.requestedLocationUpdates.postValue(false)
            stopRequestLocation()
        }
    }

    private fun stopRequestLocation() {
        logLifecycle("stopLocationUpdates")
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    private fun startRefreshEtaJob() {
        if (routeListAdapter.list.isNotEmpty()) {
            logLifecycle("startRefreshEtaJob")
            refreshEtaJob = lifecycleScope.launchPeriodicAsync(GeneralUtils.ETA_REFRESH_TIME) {
                lifecycleScope.launch {
                    viewModel.etaRequested.emit(true)
                }
            }
        }
    }

    private fun stopRefreshEtaJob() {
        refreshEtaJob?.cancel()
        refreshEtaJob = null
    }

    private fun goToStreetMapsActivity(stop: SearchMapStop) {
        activity?.let { activity ->
            val gmmIntentUri = Uri.parse("google.streetview:cbll=${stop.lat},${stop.lng}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity.packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun goToNavigationActivity(stop: SearchMapStop) {
        activity?.let { activity ->
            val gmmIntentUri = Uri.parse("google.navigation:q=${stop.lat},${stop.lng}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(activity.packageManager) != null) {
                startActivity(mapIntent)
            }
        }
    }

    private fun zoomToMyLocation(location: Location?) {
        logLifecycle("zoomToMyLocation location: $location")
        if (location == null) return
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)),
            500,
            null
        )
    }
}
