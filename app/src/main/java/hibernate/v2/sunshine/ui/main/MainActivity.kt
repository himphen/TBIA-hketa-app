package hibernate.v2.sunshine.ui.main

import android.os.Bundle
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getActivityViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.viewPager.adapter = MainViewPagerAdapter(supportFragmentManager)
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)

//        setupAlarm()
    }
}