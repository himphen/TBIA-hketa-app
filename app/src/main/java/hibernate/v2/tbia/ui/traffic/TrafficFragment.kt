package hibernate.v2.tbia.ui.traffic

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.tbia.databinding.LbFragmentTrafficBinding
import hibernate.v2.tbia.model.TrafficLocation
import hibernate.v2.tbia.ui.base.BaseFragment
import hibernate.v2.tbia.util.launchPeriodicAsync
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TrafficFragment : BaseFragment<LbFragmentTrafficBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LbFragmentTrafficBinding.inflate(inflater, container, false)

    private var refreshMapJob: Deferred<Unit>? = null
    private var googleMap: GoogleMap? = null
    private val viewModel by inject<TrafficViewModel>()
    private val locationList = arrayListOf<TrafficLocation>()
    var index = 0

    companion object {
        private const val REFRESH_TIME = 15L * 1000
        fun getInstance() = TrafficFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initData()
    }

    private fun initUI() {
        lifecycleScope.launch {
            initMap()
        }
    }

    private suspend fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(viewBinding!!.map.id) as SupportMapFragment
        googleMap = mapFragment.awaitMap()

        // TKO
        locationList.add(
            TrafficLocation(
                "將軍澳隧道",
                LatLngBounds(LatLng(22.312927, 114.246113), LatLng(22.321684, 114.262467))
            )
        )
        // Choi Hung
        locationList.add(
            TrafficLocation(
                "彩虹",
                LatLngBounds(LatLng(22.328550, 114.207262), LatLng(22.336399, 114.223178))
            )
        )
        // Wong Tai Sin
        locationList.add(
            TrafficLocation(
                "黃大仙",
                LatLngBounds(LatLng(22.338078, 114.187849), LatLng(22.344369, 114.201274))
            )
        )
        // Lung Cheung Road
        locationList.add(
            TrafficLocation(
                "龍翔道",
                LatLngBounds(LatLng(22.337313, 114.142713), LatLng(22.344762, 114.168350))
            )
        )

        refreshMapJob = initRefreshMapJob()
        googleMap?.isTrafficEnabled = true
    }

    private fun initData() {
    }

    override fun onResume() {
        super.onResume()

        if (googleMap != null && refreshMapJob == null) {
            refreshMapJob = initRefreshMapJob()
        }
    }

    @Suppress("DeferredIsResult")
    private fun initRefreshMapJob() =
        lifecycleScope.launchPeriodicAsync(REFRESH_TIME) {
            if (locationList.isEmpty()) return@launchPeriodicAsync

            var nextLocation = locationList.getOrNull(++index)
            if (nextLocation == null) {
                index = 0
                nextLocation = locationList[index]
            }
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(nextLocation.bounds, 10))
            viewBinding?.locationNameTv?.text = nextLocation.name
            ObjectAnimator.ofInt(viewBinding?.progressHorizontal, "progress", 100000, 0)
                .apply {
                    duration = REFRESH_TIME
                    start()
                }
        }

    override fun onPause() {
        super.onPause()
        refreshMapJob?.cancel()
        refreshMapJob = null
    }
}
