package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.ui.searchmap.SearchMapFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseFragmentActivity<ActivityMainBinding>() {
    override fun getActivityViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    override var fragment: Fragment? = MainFragment()

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    override fun onBackPressed() {
        mainViewModel.selectedTab.value?.let {
            (fragment?.childFragmentManager?.findFragmentByTag("f${it.position}") as? SearchMapFragment)?.let { fragment ->
                // TODO should use view model MutableLiveData
                if (fragment.isBottomSheetShown()) {
                    fragment.closeBottomSheet()
                    return
                }
            }
        }

        super.onBackPressed()
    }
}