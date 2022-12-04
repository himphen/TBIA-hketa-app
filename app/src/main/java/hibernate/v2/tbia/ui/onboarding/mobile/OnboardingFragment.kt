package hibernate.v2.tbia.ui.onboarding.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.model.checksum.FailedCheckType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.FragmentOnboardingBinding
import hibernate.v2.tbia.ui.base.BaseFragment
import hibernate.v2.tbia.ui.main.mobile.MainActivity
import hibernate.v2.tbia.ui.onboarding.OnboardingViewModel
import hibernate.v2.tbia.util.GeneralUtils
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.visible
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
        viewModel.fetchTransportDataCannotInit.observe(viewLifecycleOwner) {
            viewBinding?.logoIv?.gone(true)
            viewBinding?.loadingCl?.visible(true)
            viewBinding?.animationView?.apply {
                visible()
                playAnimation()
            }
            viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_app_token)
        }

        viewModel.fetchTransportDataRequired.observe(viewLifecycleOwner) {
            if (it < 0) return@observe

            if (it > 0) {
                showLoading()
            } else {
                goToMainActivity()
            }
        }

        viewModel.fetchTransportDataCompletedCount.observe(viewLifecycleOwner) {
            val total = viewModel.fetchTransportDataRequired.value ?: 0

            viewBinding?.loadingTv?.text = getString(
                R.string.test_onboarding_loading,
                it,
                total
            )
        }

        viewModel.fetchTransportDataCompleted.observe(viewLifecycleOwner) {
            if (viewModel.fetchTransportDataFailedList.isNotEmpty()) {
                showLoading()
                showRetryButton()
                when (viewModel.fetchTransportDataFailedList.first()) {
                    FailedCheckType.KMB -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_kmb)
                    }
                    FailedCheckType.CTB -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_ctb)
                    }
                    FailedCheckType.GMB -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_gmb)
                    }
                    FailedCheckType.MTR -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_mtr)
                    }
                    FailedCheckType.LRT -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_lrt)
                    }
                    FailedCheckType.NLB -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_nlb)
                    }
                    FailedCheckType.OTHER -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_other)
                    }
                    FailedCheckType.CHECKSUM -> {
                        viewBinding?.loadingTv?.setText(R.string.test_onboarding_loading_failed_checksum)
                    }
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000)
                    goToMainActivity()
                }
            }
        }
    }

    fun initUI() {
        viewBinding?.retryButton?.setOnClickListener {
            hideLoading()
            hideRetryButton()
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                viewModel.checkDbTransportData()
            }
        }

        viewBinding?.reportButton?.setOnClickListener {
            GeneralUtils.report(it.context)
        }
    }

    fun initData() {
        viewModel.checkDbTransportData()
    }

    private fun showLoading() {
        viewBinding?.logoIv?.gone(true)
        viewBinding?.loadingCl?.visible(true)
        viewBinding?.animationView?.apply {
            visible()
            playAnimation()
        }
    }

    private fun hideLoading() {
        viewBinding?.logoIv?.visible(true)
        viewBinding?.loadingCl?.gone(false)
        viewBinding?.animationView?.apply {
            gone(false)
            cancelAnimation()
        }
    }

    private fun showRetryButton() {
        viewBinding?.failedButtonLl?.visible(true)
    }

    private fun hideRetryButton() {
        viewBinding?.failedButtonLl?.gone(false)
    }

    private fun goToMainActivity() {
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }
}
