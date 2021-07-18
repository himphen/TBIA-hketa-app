package hibernate.v2.sunshine.ui.settings.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.ui.settings.eta.listing.mobile.SettingsEtaActivity
import hibernate.v2.sunshine.util.GeneralUtils
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>("pref_settings_bus_eta")?.setOnPreferenceClickListener {
            startActivity(Intent(context, SettingsEtaActivity::class.java))

            true
        }

        findPreference<Preference>("pref_settings_eta_layout")?.setOnPreferenceClickListener {
            startActivity(Intent(context, SettingsEtaActivity::class.java))

            true
        }
        findPreference<Preference>("pref_settings_version")?.apply {
            summary = GeneralUtils.getAppVersionName(context)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }
}