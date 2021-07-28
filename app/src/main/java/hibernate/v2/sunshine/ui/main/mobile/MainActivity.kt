package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import kotlinx.coroutines.launch
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
        if (mainViewModel.selectedTab.value == MainViewPagerAdapter.TabType.SearchMap) {
            if (!mainViewModel.isBottomSheetClosed()) {
                lifecycleScope.launch {
                    mainViewModel.onRequestedCloseBottomSheet.emit(Unit)
                }
                return
            }
        }

        super.onBackPressed()
    }
}