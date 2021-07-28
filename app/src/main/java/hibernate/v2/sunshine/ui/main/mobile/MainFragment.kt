package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import hibernate.v2.sunshine.databinding.FragmentMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.util.slideToBottomAnimate
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {
        const val bottomBarHeight = 120
    }

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initEvent()
    }

    private fun initEvent() {
        mainViewModel.onStopBottomSheetStateChanged.observe(viewLifecycleOwner) {
            viewBinding?.bottomBar?.slideToBottomAnimate(mainViewModel.isBottomSheetClosed())
        }
        mainViewModel.onRouteBottomSheetStateChanged.observe(viewLifecycleOwner) {
            viewBinding?.bottomBar?.slideToBottomAnimate(mainViewModel.isBottomSheetClosed())
        }
    }

    private fun initUI() {
        val viewBinding = viewBinding!!
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
}