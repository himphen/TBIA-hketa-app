package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.ui.stopmap.StopMapFragment

class MainActivity : BaseFragmentActivity<ActivityMainBinding>() {
    override fun getActivityViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    override var fragment: Fragment? = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onBackPressed() {
        (fragment?.childFragmentManager?.findFragmentByTag("f2") as? StopMapFragment)?.let {
            if (it.isBottomSheetShown()) {
                it.closeBottomSheet()
                return
            }
        }

        super.onBackPressed()
    }
}