package hibernate.v2.sunshine.ui.main.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.ui.route.list.mobile.RouteListActivity
import hibernate.v2.sunshine.util.slideToBottomAnimate
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseFragmentActivity<ActivityMainBinding>() {

    companion object {
        const val bottomBarHeight = 120
        const val ACTIVITY_RESULT_SAVED_BOOKMARK = 10
    }

    override fun getActivityViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    var etaUpdatedLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ACTIVITY_RESULT_SAVED_BOOKMARK) {
                isResetLoadingBookmarkList = true
                lifecycleScope.launch {
                    mainViewModel.onUpdatedEtaList.emit(Unit)
                }
            }
        }

    var isResetLoadingBookmarkList = false
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
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

            mainViewModel.selectedTab.value = MainViewPagerAdapter.TabType.Eta

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val newPosition = if (position == 0) 0 else position + 1
                    val selectedItem = bottomBar.menu.items[newPosition]
                    bottomBar.menu.select(selectedItem.id)

                    mainViewModel.selectedTab.value =
                        MainViewPagerAdapter.TabType.fromPosition(position)
                }
            })
        }

        bottomBar.apply {
            onItemSelectedListener = { _, menuItem, byUser ->
                if (menuItem.id == R.id.menu_search) {
                    etaUpdatedLauncher.launch(
                        Intent(
                            context,
                            RouteListActivity::class.java
                        )
                    )

                    mainViewModel.selectedTab.value?.let {
                        bottomBar.menu.select(it.menuItemId)
                    }
                } else {
                    MainViewPagerAdapter.TabType
                        .fromMenuItemId(menuItem.id)?.let { tabType ->
                            if (byUser)
                                viewBinding.viewPager.setCurrentItem(tabType.position, false)
                        }
                }
            }
        }
    }

    private fun initEvent() {
        mainViewModel.onStopBottomSheetStateChanged.distinctUntilChanged().observe(this) {
            val show = mainViewModel.isBottomSheetClosed()
            viewBinding.bottomBar.slideToBottomAnimate(show)
        }
        mainViewModel.onRouteBottomSheetStateChanged.distinctUntilChanged().observe(this) {
            val show = mainViewModel.isBottomSheetClosed()
            viewBinding.bottomBar.slideToBottomAnimate(show)
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
