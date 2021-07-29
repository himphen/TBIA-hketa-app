package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.util.slideToBottomAnimate
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseFragmentActivity<ActivityMainBinding>() {

    companion object {
        const val bottomBarHeight = 120
    }

    override fun getActivityViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)

        initUI()
        initEvent()
    }

    private fun initUI() {
        val bottomBar = viewBinding.bottomBar

        val adapter = MainViewPagerAdapter(this)
        viewBinding.viewPager.apply {
            this.adapter = adapter
            isUserInputEnabled = false
            offscreenPageLimit = 1

            mainViewModel.selectedTab.value = MainViewPagerAdapter.TabType.Eta

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val selectedItem = bottomBar.menu.items[position]
                    bottomBar.menu.select(selectedItem.id)

                    mainViewModel.selectedTab.value =
                        MainViewPagerAdapter.TabType.fromPosition(position)
                }
            })
        }

        bottomBar.apply {
            onItemSelectedListener = { _, menuItem, byUser ->
                MainViewPagerAdapter.TabType
                    .fromMenuItemId(menuItem.id)?.let { tabType ->
                        if (byUser)
                            viewBinding.viewPager.setCurrentItem(tabType.position, false)
                    }
            }
        }
    }

    private fun initEvent() {
        mainViewModel.onStopBottomSheetStateChanged.observe(this) {
            viewBinding.bottomBar.slideToBottomAnimate(mainViewModel.isBottomSheetClosed())
        }
        mainViewModel.onRouteBottomSheetStateChanged.observe(this) {
            viewBinding.bottomBar.slideToBottomAnimate(mainViewModel.isBottomSheetClosed())
        }
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