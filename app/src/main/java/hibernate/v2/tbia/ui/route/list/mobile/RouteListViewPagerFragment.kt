package hibernate.v2.tbia.ui.route.list.mobile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.databinding.FragmentRouteListViewPagerBinding
import hibernate.v2.tbia.ui.base.BaseActivity
import hibernate.v2.tbia.ui.base.BaseFragment
import hibernate.v2.tbia.util.afterTextChanged
import hibernate.v2.tbia.util.onTextChanged
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RouteListViewPagerFragment : BaseFragment<FragmentRouteListViewPagerBinding>() {
    companion object {
        const val kDrawableRight = 2
        const val searchDelay = 500L
    }

    private val drawableRight: Drawable?
        get() {
            val viewBinding = viewBinding ?: return null

            return if (viewBinding.searchEt.compoundDrawables.asList().size > kDrawableRight) {
                viewBinding.searchEt.compoundDrawables[kDrawableRight]
            } else {
                null
            }
        }

    private val viewModel: RouteListMobileViewModel by sharedViewModel()
    private var etaType: EtaType = EtaType.KMB

    private val preferences by inject<SharedPreferencesManager>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentRouteListViewPagerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initUi()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        val adapter = RouteListViewPagerAdapter(this)
        viewBinding.viewPager.adapter = adapter
        viewBinding.viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        viewBinding.viewPager.isUserInputEnabled = false
        viewBinding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val item = adapter.list[tab.position]

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.tabItemSelectedLiveData.emit(item)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        viewBinding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    etaType = adapter.list[position]
                }
            })

        TabLayoutMediator(
            viewBinding.tabLayout,
            viewBinding.viewPager,
            true,
            false
        ) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()

        // Default tab
        var position = preferences.defaultCompany
        if (position >= adapter.list.size) {
            preferences.defaultCompany = 0
            position = 0
        }
        viewBinding.viewPager.setCurrentItem(position, false)

        viewBinding.searchEt.apply {
            filters += InputFilter.AllCaps()
            isLongClickable = false
            setOnTouchListener(
                View.OnTouchListener { _, event ->
                    drawableRight?.let {
                        if (event.action == MotionEvent.ACTION_UP) {
                            if (event.rawX >= right - it.bounds.width()
                            ) {
                                text.clear()
                                performClick()
                                return@OnTouchListener true
                            }
                        }
                    }
                    false
                }
            )

            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    (activity as? BaseActivity<out ViewBinding>)?.hideSoftKeyboard()
                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }

            onTextChanged()
                .onEach {
                    updateCloseButton()
                }
                .launchIn(lifecycleScope)

            afterTextChanged()
                .debounce(searchDelay)
                .onEach {
                    viewModel.searchRouteKeyword = it
                    viewModel.searchRoute(context, etaType)
                }
                .launchIn(lifecycleScope)

            doOnLayout {
                // initial hide close button
                updateCloseButton()
            }

            if (requestFocus()) {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    private fun initEvent() {
        viewModel.tabItemSelectedLiveData.onEach {
            context?.let { context ->
                val color = it.color().colorInt()
                viewBinding?.tabLayout?.setSelectedTabIndicatorColor(color)
                viewBinding?.searchCl?.setBackgroundColor(color)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateCloseButton() {
        val viewBinding = viewBinding ?: return
        if (viewBinding.searchEt.text.isEmpty()) {
            drawableRight?.alpha = 0
        } else {
            drawableRight?.alpha = 100
        }
    }
}
