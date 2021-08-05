package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaViewPagerBinding
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.base.BaseActivity
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.util.afterTextChanged
import hibernate.v2.sunshine.util.onTextChanged
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaViewPagerFragment : BaseFragment<FragmentAddEtaViewPagerBinding>() {
    companion object {
        const val searchDelay = 500L
    }

    private val drawableRight: Drawable?
        get() {
            val viewBinding = viewBinding ?: return null

            return if (viewBinding.searchEt.compoundDrawables.asList().size > AddEtaRouteFragment.kDrawableRight) {
                viewBinding.searchEt.compoundDrawables[AddEtaRouteFragment.kDrawableRight]
            } else {
                null
            }
        }

    private val viewModel: AddEtaViewModel by sharedViewModel()
    private var etaType: EtaType = EtaType.KMB

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentAddEtaViewPagerBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        val adapter = AddEtaViewPagerAdapter(this)
        viewBinding.viewPager.adapter = adapter
        viewBinding.viewPager.offscreenPageLimit = 1
        viewBinding.viewPager.isUserInputEnabled = false
        viewBinding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val colorRes = when (tab.position) {
                        0 -> R.color.brand_color_kmb
                        1 -> R.color.brand_color_nwfb
                        2 -> R.color.brand_color_ctb
                        3, 4, 5 -> R.color.brand_color_gmb
                        else -> R.color.brand_color_gmb
                    }

                    context?.let { context ->
                        viewBinding.tabLayout.setSelectedTabIndicatorColor(context.getColor(colorRes))
                    }

                    viewBinding.searchCl.setBackgroundResource(colorRes)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        viewBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                etaType = adapter.list[position]

//                viewBinding.searchEt.setText("")
                viewModel.searchRoute(etaType)
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

        viewBinding.searchEt.apply {
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
                    viewModel.searchRouteKeyword.value = it
                    viewModel.searchRoute(etaType)
                }
                .launchIn(lifecycleScope)

            doOnLayout {
                // initial hide close button
                updateCloseButton()
            }
        }
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