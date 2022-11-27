package hibernate.v2.sunshine.ui.settings.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import hibernate.v2.MR
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.AdManager
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.onboarding.mobile.OnboardingActivity
import hibernate.v2.sunshine.ui.settings.eta.layout.mobile.EtaLayoutSelectionActivity
import hibernate.v2.sunshine.util.GeneralUtils
import hibernate.v2.sunshine.util.GeneralUtils.report
import hibernate.v2.sunshine.util.dpToPx
import hibernate.v2.utils.localized
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.Date

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val AD_TIME = 2 * 24 * 60 * 60 * 1000 // 2 days
    }

    private val onboardingViewModel by inject<OnboardingViewModel>()
    private val preferences by inject<SharedPreferencesManager>()
    private val adManager by inject<AdManager>()

    private val defaultCompanyArray: Array<String> by lazy {
        arrayListOf(
            MR.strings.add_eta_brand_selection_kmb_btn,
            MR.strings.add_eta_brand_selection_nwfb_btn,
            MR.strings.add_eta_brand_selection_ctb_btn,
            MR.strings.add_eta_brand_selection_gmb_btn,
            MR.strings.add_eta_brand_selection_gmb_hki_btn,
            MR.strings.add_eta_brand_selection_gmb_kln_btn,
            MR.strings.add_eta_brand_selection_gmb_nt_btn,
            MR.strings.add_eta_brand_selection_mtr_btn,
            MR.strings.add_eta_brand_selection_lrt_btn,
            MR.strings.add_eta_brand_selection_nlb_btn,
        ).map { it.localized(requireContext()) }.toTypedArray()
    }

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

        findPreference<Preference>("pref_settings_default_company")?.apply {
            updateDefaultCompany()

            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(it.context)
                    .setTitle(MR.strings.title_settings_default_company.localized(context))
                    .setItems(defaultCompanyArray) { dialog, which ->
                        preferences.defaultCompany = which
                        updateDefaultCompany()
                        dialog.dismiss()
                    }
                    .setNegativeButton(MR.strings.dialog_cancel_btn.localized(context)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
        }

        findPreference<CheckBoxPreference>("pref_settings_hide_ad_banner")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                val value = newValue as Boolean
                if (value) {
                    val newTime = System.currentTimeMillis() + AD_TIME
                    preferences.hideAdBannerUntil = newTime
                    setAdSummaryOnText(newTime)
                } else {
                    preferences.hideAdBannerUntil = 0
                }

                Snackbar.make(
                    requireView(),
                    MR.strings.settings_hide_ad_toast_message.localized(context),
                    Snackbar.LENGTH_LONG
                ).show()
                true
            }
        }

        findPreference<Preference>("pref_settings_lang")?.apply {
            setOnPreferenceClickListener {
                openDialogLanguage()

                true
            }
        }

        findPreference<Preference>("pref_settings_report")?.apply {
            setOnPreferenceClickListener {
                report(context)

                true
            }
        }

        findPreference<Preference>("pref_settings_version")?.apply {
            summary = GeneralUtils.getAppVersionName(context)
        }
        findPreference<Preference>("pref_settings_reset")?.apply {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(it.context)
                    .setMessage(MR.strings.dialog_settings_reset_message.localized(context))
                    .setPositiveButton(MR.strings.dialog_confirm_btn.localized(context)) { dialog, _ ->
                        lifecycleScope.launch {
                            onboardingViewModel.resetTransportData()
                            dialog.dismiss()
                            startActivity(Intent(activity, OnboardingActivity::class.java))
                            activity?.finish()
                        }
                    }
                    .setNegativeButton(MR.strings.dialog_cancel_btn.localized(context)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView.apply {
            setPadding(paddingLeft, paddingTop, paddingRight, dpToPx(120))
            clipToPadding = false
        }
    }

    override fun onResume() {
        super.onResume()

        // Check hide ad expire time
        findPreference<CheckBoxPreference>("pref_settings_hide_ad_banner")?.apply {
            if (adManager.shouldShowBannerAd()) {
                isChecked = false
            } else {
                isChecked = true
                setAdSummaryOnText(preferences.hideAdBannerUntil)
            }
        }
    }

    private fun CheckBoxPreference.setAdSummaryOnText(value: Long) {
        summaryOn = MR.strings.settings_hide_ad_summary.localized(
            requireContext(),
            Date().run {
                time = value
                SimpleDateFormat("yyyy-MM-dd HH:mm").format(this)
            }
        )
    }

    private fun Preference.updateDefaultCompany() {
        val index = preferences.defaultCompany

        summary = defaultCompanyArray.getOrNull(index)
    }

    private fun openDialogLanguage() {
        val activity = activity ?: return
        MaterialAlertDialogBuilder(activity)
            .setTitle(MR.strings.title_settings_language.localized(requireContext()))
            .setItems(R.array.language_choose) { dialog, index ->
                dialog.dismiss()
                val languageLocaleCodeArray = resources.getStringArray(R.array.language_locale_code)
                preferences.language = languageLocaleCodeArray[index]

                val intent = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
                intent?.let {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .setNegativeButton(MR.strings.dialog_cancel_btn.localized(requireContext()), null)
            .show()
    }
}
