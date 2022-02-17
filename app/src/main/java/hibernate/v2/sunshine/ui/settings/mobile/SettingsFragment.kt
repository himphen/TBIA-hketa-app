package hibernate.v2.sunshine.ui.settings.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.onboarding.mobile.OnboardingActivity
import hibernate.v2.sunshine.ui.settings.eta.layout.mobile.EtaLayoutSelectionActivity
import hibernate.v2.sunshine.util.GeneralUtils
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val onboardingViewModel by inject<OnboardingViewModel>()

    private var etaLayoutLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launchWhenResumed {
                    mainViewModel.onUpdatedEtaLayout.emit(Unit)
                }
            }
        }

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        findPreference<Preference>("pref_settings_eta_layout")?.setOnPreferenceClickListener {
            etaLayoutLauncher.launch(Intent(context, EtaLayoutSelectionActivity::class.java))
            true
        }
        findPreference<Preference>("pref_settings_version")?.apply {
            summary = GeneralUtils.getAppVersionName(context)
        }
        findPreference<Preference>("pref_settings_reset")?.apply {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(it.context)
                    .setMessage(R.string.dialog_settings_reset_message)
                    .setPositiveButton(R.string.dialog_confirm_btn) { dialog, _ ->
                        lifecycleScope.launch {
                            onboardingViewModel.resetTransportData()
                            dialog.dismiss()
                            startActivity(Intent(activity, OnboardingActivity::class.java))
                            activity?.finish()
                        }
                    }
                    .setNegativeButton(R.string.dialog_cancel_btn) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
        }
    }
}
