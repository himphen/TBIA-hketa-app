package hibernate.v2.sunshine.ui.main

import android.os.Bundle
import hibernate.v2.sunshine.databinding.ActivityEtaBinding
import hibernate.v2.sunshine.ui.base.BaseActivity
import hibernate.v2.sunshine.ui.eta.EtaFragment
import hibernate.v2.sunshine.ui.traffic.TrafficFragment
import hibernate.v2.sunshine.ui.weather.WeatherFragment

class MainActivity : BaseActivity<ActivityEtaBinding>() {
    override fun getActivityViewBinding() = ActivityEtaBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(viewBinding.containerTopLeft.id, EtaFragment())
            .replace(viewBinding.containerTopRight.id, TrafficFragment())
            .replace(viewBinding.containerBottom.id, WeatherFragment())
            .commitNow()
    }
}