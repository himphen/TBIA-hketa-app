package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.sunshine.databinding.FragmentViewPagerContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragment

class MainFragment : BaseFragment<FragmentViewPagerContainerBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentViewPagerContainerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        val adapter = MainViewPagerAdapter(this)
        viewBinding.viewPager.apply {
            this.adapter = adapter
            offscreenPageLimit = 5
            isUserInputEnabled = false
        }
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()
    }
}