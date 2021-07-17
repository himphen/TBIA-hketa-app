package hibernate.v2.sunshine.ui.onboarding.mobile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hibernate.v2.sunshine.databinding.FragmentOnboardingBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.ui.onboarding.FetchTransportDataType
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
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
                    Toast.makeText(context, "Done KMB", Toast.LENGTH_LONG).show()
                }
                FetchTransportDataType.NC -> {
                    Toast.makeText(context, "Done NC", Toast.LENGTH_LONG).show()
                }
                FetchTransportDataType.ALL -> {
                    goToMainActivity()
                }
                else -> {
                }
            }
        }

        viewModel.fetchTransportDataFailed.observe(viewLifecycleOwner) {
            when (it) {
                FetchTransportDataType.KMB -> {
                    Toast.makeText(context, "Failed KMB", Toast.LENGTH_LONG).show()
                }
                FetchTransportDataType.NC -> {
                    Toast.makeText(context, "Failed NC", Toast.LENGTH_LONG).show()
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