package hibernate.v2.sunshine.ui.main.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentMainBinding
import hibernate.v2.sunshine.ui.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        val viewBinding = viewBinding!!
        val bottomBar = viewBinding.bottomBar

        val adapter = MainViewPagerAdapter(this)
        viewBinding.viewPager.apply {
            this.adapter = adapter
            offscreenPageLimit = 5

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val selectedItem = bottomBar.menu.items[position]
                    bottomBar.menu.select(selectedItem.id)
                }
            })
        }

        bottomBar.apply {
            onItemSelectedListener = { view, menuItem, byUser ->
                when (menuItem.id) {
                    R.id.home -> {
                        if (byUser)
                            viewBinding.viewPager.setCurrentItem(0, true)
                    }
                    R.id.settings -> {
                        if (byUser)
                            viewBinding.viewPager.setCurrentItem(1, true)
                    }
                }
            }
        }
    }
}