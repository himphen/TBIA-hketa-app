package hibernate.v2.sunshine.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.databinding.FragmentOnboardingBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private val viewModel by inject<OnboardingViewModel>()

    companion object {
        fun getInstance() = OnboardingFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentOnboardingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initUI()
        initData()
    }

    fun initEvent() {
        viewModel.fetchTransportDataRequired.onEach {
            if (it) {
                // show download ui
            } else {

            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.fetchTransportDataCompleted.onEach {
            if (it) {
                // show download ui
            } else {

            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    fun initUI() {
    }

    fun initData() {
        lifecycleScope.launch {
            viewModel.checkDbTransportData(lifecycleScope)
        }
    }
}