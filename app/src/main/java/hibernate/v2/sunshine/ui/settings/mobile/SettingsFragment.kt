package hibernate.v2.sunshine.ui.settings.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.onboarding.mobile.OnboardingActivity
import hibernate.v2.sunshine.ui.settings.eta.layout.mobile.EtaLayoutSelectionActivity
import hibernate.v2.sunshine.util.GeneralUtils
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.Date

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val AD_TIME = 2 * 24 * 60 * 60 * 1000
    }

    private val onboardingViewModel by inject<OnboardingViewModel>()
    private val preferences by inject<SharedPreferencesManager>()

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

//        findPreference<CheckBoxPreference>("pref_settings_map_traffic_toggle")?.apply {
//            isChecked = preferences.trafficLayerToggle
//
//            setOnPreferenceChangeListener { _, newValue ->
//                val value = newValue as Boolean
//                preferences.trafficLayerToggle = value
//                viewLifecycleOwner.lifecycleScope.launch{
//                    mainViewModel.onChangedTrafficLayerToggle.emit(value)
//                }
//                true
//            }
//        }

        findPreference<CheckBoxPreference>("pref_settings_hide_ad_banner")?.apply {
            fun setSummaryOnText(value: Long) {
                summaryOn = getString(R.string.settings_hide_ad_summary, Date().run {
                    time = value
                    SimpleDateFormat("yyyy-MM-dd HH:mm").format(this)
                })
            }

            val time = preferences.hideAdBanner
            isChecked = time != 0L
            if (isChecked) {
                setSummaryOnText(time)
            }

            setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as Boolean
                if (value) {
                    val newTime = System.currentTimeMillis() + AD_TIME
                    preferences.hideAdBanner = newTime
                    setSummaryOnText(newTime)
                } else {
                    preferences.hideAdBanner = 0
                }
                Toast.makeText(context, R.string.settings_hide_ad_toast_message, Toast.LENGTH_LONG)
                    .show()
                true
            }
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
