package hibernate.v2.sunshine.ui.traffic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.awaitMap
import hibernate.v2.sunshine.databinding.FragmentTrafficBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class TrafficFragment : BaseFragment<FragmentTrafficBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTrafficBinding.inflate(inflater, container, false)

    private var googleMap: GoogleMap? = null
    private val viewModel by inject<TrafficViewModel>()

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
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(22.3412175, 114.1594063),
                13f
            )
        )
        googleMap?.isTrafficEnabled = true
    }

    private fun initData() {
    }
}