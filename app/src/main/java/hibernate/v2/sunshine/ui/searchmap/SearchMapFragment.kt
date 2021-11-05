package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.fonfon.kgeohash.GeoHash
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
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentSearchMapBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.eta.home.EtaViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.launchPeriodicAsync
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Date
import kotlin.coroutines.resume

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
    private val addEtaViewModel: AddEtaViewModel by inject()
    private val etaViewModel: EtaViewModel by inject()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private var clusterManager: CustomClusterManager? = null

    private var routeBottomSheetEtaCardList: MutableList<Card.EtaCard> = mutableListOf()
    private var refreshEtaJob: Deferred<Unit>? = null

    private val routeListAdapter = RouteListAdapter { card: Card.EtaCard ->
        viewLifecycleOwner.lifecycleScope.launch {
            val context = context ?: return@launch
            val result = suspendCancellableCoroutine<Boolean> { cont ->
                MaterialAlertDialogBuilder(context)
                    .setMessage(
                        getString(
                            R.string.dialog_add_eta_in_search_map_description_bus,
                            card.route.routeNo,
                            card.stop.nameTc
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
                addEtaViewModel.saveStop(addCard)
            }
        }
    }

    private val stopListAdapter = StopListAdapter { searchMapStop: SearchMapStop ->
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(searchMapStop.position, 19f))
        showRouteBottomSheet(stop = searchMapStop)
    }

    private var googleMap: GoogleMap? = null

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
        viewModel.stopList.onEach {
            showStopListOnMap(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.routeListForBottomSheet.observe(viewLifecycleOwner) {
            showRouteListOnBottomSheet(it)
        }

        viewModel.stopListForBottomSheet.observe(viewLifecycleOwner) {
            showStopListOnBottomSheet(it)
        }

        addEtaViewModel.isAddEtaSuccessful.onEach {
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
                stopRefreshEtaJob()
                routeListAdapter.setData(mutableListOf())
            }
        }

        mainViewModel.onStopBottomSheetStateChanged.observe(viewLifecycleOwner) {
            if (it == BottomSheetBehavior.STATE_HIDDEN) {
                stopRefreshEtaJob()
                stopListAdapter.setData(mutableListOf())
            }
        }

        etaViewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            routeBottomSheetEtaCardList.clear()
            routeBottomSheetEtaCardList.addAll(it)
            processEtaList()
        }
        etaViewModel.etaUpdateError.onEach {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
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
        googleMap = mapFragment.awaitMap().apply {
            setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_stop_map)
            )
//            isTrafficEnabled = true
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
                googleMap.setOnCameraMoveListener(clusterManager)

                updateCurrentMarkerCircle()
            }
        }
    }

    private fun showRouteBottomSheet(stop: SearchMapStop) {
        viewModel.selectedStop.value = stop
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutRouteList.stopNameTv.text = stop.nameTc
        }
        routeListAdapter.setData(mutableListOf())
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStop(stop.etaType, stop)
    }

    private fun showStopBottomSheet(list: List<SearchMapStop>?) {
        if (list == null) return
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStopList(list)
    }

    private fun showStopListOnBottomSheet(list: List<SearchMapStop>?) {
        stopListAdapter.setData(list?.toMutableList())
    }

    private fun showRouteListOnBottomSheet(list: List<Card.EtaCard>) {
        routeBottomSheetEtaCardList.clear()
        routeBottomSheetEtaCardList.addAll(list)
        routeListAdapter.setData(routeBottomSheetEtaCardList)
        processEtaList()

        restartRefreshEtaJob()
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
    }

    override fun onPause() {
        stopRefreshEtaJob()
        super.onPause()
    }

    private fun processEtaList() {
        val etaCardList = routeBottomSheetEtaCardList
        if (!etaCardList.isNullOrEmpty()) {
            etaCardList.forEachIndexed { index, etaCard ->
                val temp = etaCard.etaList.filter { eta: TransportEta ->
                    eta.eta?.let { etaDate ->
                        val currentDate = Date()
                        DateUtil.getTimeDiffInMin(
                            etaDate,
                            currentDate
                        ) > 0
                    } ?: run {
                        false
                    }
                }

                etaCard.etaList.clear()
                etaCard.etaList.addAll(temp)

                routeListAdapter.replace(index, etaCard)
            }
        }
    }

    private fun restartRefreshEtaJob() {
        stopRefreshEtaJob()
        startRefreshEtaJob()
    }

    private fun startRefreshEtaJob() {
        if (routeBottomSheetEtaCardList.isNotEmpty()) {
            refreshEtaJob =
                CoroutineScope(Dispatchers.Main).launchPeriodicAsync(EtaViewModel.REFRESH_TIME) {
                    etaViewModel.updateEtaList(routeBottomSheetEtaCardList)
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
}
