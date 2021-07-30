package hibernate.v2.sunshine.ui.onboarding.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentOnboardingBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.ui.onboarding.FetchTransportDataType
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        viewModel.fetchTransportDataRequired.observe(viewLifecycleOwner) {
            if (it) {
                // show download ui
                viewModel.downloadTransportData()
            } else {
                goToMainActivity()
            }
        }

        viewModel.fetchTransportDataCompleted.observe(viewLifecycleOwner) {
            when (it) {
                FetchTransportDataType.KMB -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_kmb)
                }
                FetchTransportDataType.NC -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_ctb)
                }
                FetchTransportDataType.GMB -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_gmb)
                }
                FetchTransportDataType.ALL -> {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        delay(1000)
                        goToMainActivity()
                    }
                }
                else -> {
                }
            }
        }

        viewModel.fetchTransportDataFailed.observe(viewLifecycleOwner) {
            when (it) {
                FetchTransportDataType.KMB -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_kmb)
                }
                FetchTransportDataType.NC -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_ctb)
                }
                FetchTransportDataType.GMB -> {
                    viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_gmb)
                }
                else -> {
                }
            }
        }
    }

    fun initUI() {

    }

    fun initData() {
        viewModel.checkDbTransportData()
    }

    private fun goToMainActivity() {
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }
}