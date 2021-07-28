package hibernate.v2.sunshine.ui.searchmap

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import hibernate.v2.sunshine.databinding.FragmentSearchMapBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.searchmap.SearchMapRoute
import hibernate.v2.sunshine.model.searchmap.SearchMapStop
import hibernate.v2.sunshine.model.transport.TransportStop
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.resume

class SearchMapFragment : BaseFragment<FragmentSearchMapBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSearchMapBinding.inflate(inflater, container, false)

    private lateinit var stopListBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var routeListBottomSheetBehavior: BottomSheetBehavior<*>

    private val viewModel: SearchMapViewModel by inject()
    private val etaViewModel: AddEtaViewModel by inject()
    private val mainViewModel: MainViewModel by sharedViewModel()

    private var clusterManager: CustomClusterManager? = null

    private val routeListAdapter = RouteListAdapter(object : RouteListAdapter.ItemListener {
        override fun onRouteSelected(searchMapRoute: SearchMapRoute) {
            val selectedStop = viewModel.selectedStop.value ?: return

            viewLifecycleOwner.lifecycleScope.launch {
                val card = Card.RouteStopAddCard(
                    searchMapRoute.route,
                    TransportStop(
                        lat = selectedStop.lat,
                        lng = selectedStop.lng,
                        nameEn = selectedStop.nameEn,
                        nameSc = selectedStop.nameSc,
                        nameTc = selectedStop.nameTc,
                        stopId = selectedStop.stopId,
                        seq = searchMapRoute.seq
                    )
                )
                val result = suspendCancellableCoroutine<Boolean> { cont ->
                    val dialog = SearchMapAddEtaDialog(
                        card,
                        { _, which ->
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                cont.resume(true)
                            }
                        }
                    )
                    dialog.show(childFragmentManager, null)
                    cont.invokeOnCancellation { dialog.dismiss() }
                }

                if (result) {
                    etaViewModel.saveStop(card)
                }
            }
        }
    })

    private val stopListAdapter = StopListAdapter(object : StopListAdapter.ItemListener {
        override fun onStopSelected(searchMapStop: SearchMapStop) {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(searchMapStop.position, 19f))
            showRouteBottomSheet(stop = searchMapStop)
        }
    })

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
        viewModel.stopList.observe(viewLifecycleOwner) {
            showStopListOnMap(it)
        }

        viewModel.routeListForBottomSheet.observe(viewLifecycleOwner) {
            showRouteListOnBottomSheet(it)
        }

        viewModel.stopListForBottomSheet.observe(viewLifecycleOwner) {
            showStopListOnBottomSheet(it)
        }

        etaViewModel.isAddEtaSuccessful.onEach {
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

    private fun initStopListBottomSheet(viewBinding: FragmentSearchMapBinding) {
        stopListBottomSheetBehavior =
            BottomSheetBehavior.from(viewBinding.layoutStopList.root).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        mainViewModel.onStopBottomSheetStateChanged.value = newState
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
                        mainViewModel.onRouteBottomSheetStateChanged.value = newState
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

    private fun showRouteBottomSheet(stop: SearchMapStop) {
        viewModel.selectedStop.value = stop
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutRouteList.stopNameTv.text = stop.nameTc
        }
        routeListAdapter.setData(mutableListOf())
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStop(stop.etaType, stop.stopId)
    }

    private fun showStopBottomSheet(list: List<SearchMapStop>?) {
        if (list == null) return
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        viewBinding?.let {
            it.layoutStopList.countTv.text =
                getString(R.string.stop_list_bottom_sheet_title, list.size)
        }

        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getRouteListFromStopList(list)
    }

    private fun showStopListOnBottomSheet(list: List<SearchMapStop>?) {
        stopListAdapter.setData(list?.toMutableList())
    }

    private fun showRouteListOnBottomSheet(list: List<SearchMapRoute>?) {
        routeListAdapter.setData(list?.toMutableList())
    }

    private fun showStopListOnMap(list: List<SearchMapStop>?) {
        clusterManager?.addItems(list)
    }

    private fun closeBottomSheet() {
        routeListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        stopListBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
