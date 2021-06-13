package hibernate.v2.sunshine.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.sunshine.databinding.FragmentMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.EtaFragment
import hibernate.v2.sunshine.ui.traffic.TrafficFragment
import hibernate.v2.sunshine.ui.weather.WeatherFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {
        fun getInstance() = MainFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(viewBinding!!.containerTopLeft.id, EtaFragment.getInstance())
            .commit()
        childFragmentManager.beginTransaction()
            .replace(viewBinding!!.containerTopRight.id, TrafficFragment.getInstance())
            .commit()
        childFragmentManager.beginTransaction()
            .replace(viewBinding!!.containerBottom.id, WeatherFragment.getInstance())
            .commit()
    }
}