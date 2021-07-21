package hibernate.v2.sunshine.ui.settings.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.eta.mobile.EtaFragment
import hibernate.v2.sunshine.ui.settings.eta.layout.mobile.EtaLayoutSelectionActivity
import hibernate.v2.sunshine.ui.settings.eta.listing.mobile.SettingsEtaListingActivity
import hibernate.v2.sunshine.util.GeneralUtils

class SettingsFragment : PreferenceFragmentCompat() {

    private var etaLayoutLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getEtaFragment()?.initAdapter()
                getEtaFragment()?.updateAdapterData()
            }
        }

    private var etaEditLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getEtaFragment()?.updateAdapterData()
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)

        findPreference<Preference>("pref_settings_bus_eta")?.setOnPreferenceClickListener {
            etaEditLauncher.launch(Intent(context, SettingsEtaListingActivity::class.java))
            true
        }

        findPreference<Preference>("pref_settings_eta_layout")?.setOnPreferenceClickListener {
            etaLayoutLauncher.launch(Intent(context, EtaLayoutSelectionActivity::class.java))
            true
        }
        findPreference<Preference>("pref_settings_version")?.apply {
            summary = GeneralUtils.getAppVersionName(context)
        }
    }

    private fun getEtaFragment(): EtaFragment? {
        return parentFragmentManager.findFragmentByTag("f0") as? EtaFragment
    }
}