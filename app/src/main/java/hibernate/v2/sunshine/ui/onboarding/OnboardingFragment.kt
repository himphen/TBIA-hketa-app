package hibernate.v2.sunshine.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import hibernate.v2.sunshine.databinding.FragmentOnboardingBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.main.MainActivity
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
                FetchTransportDataCompleted.KMB -> {
                }
                FetchTransportDataCompleted.NC -> {
                }
                FetchTransportDataCompleted.ALL -> {
                    Toast.makeText(context, "Done", Toast.LENGTH_LONG).show()
//                    goToMainActivity()
                }
                null -> {

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