package hibernate.v2.sunshine.ui.settings.lang.leanback

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import org.koin.android.ext.android.inject


class LangSelectionFragment : GuidedStepSupportFragment() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_lang_title
            ),
            "",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val items = resources.getStringArray(R.array.language_choose)
        items.forEachIndexed { index, item ->
            val action: GuidedAction = GuidedAction.Builder(context)
                .id(index.toLong())
                .title(item).build()
            actions.add(action)
        }

        val action = GuidedAction.Builder(context)
            .id(ACTION_ID_CANCEL)
            .title(getString(R.string.dialog_eta_layout_cancel_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_CANCEL -> {}
            else -> {
                val languageLocaleCodeArray = resources.getStringArray(R.array.language_locale_code)
                languageLocaleCodeArray.getOrNull(action.id.toInt())?.also {
                    sharedPreferencesManager.language = it

                    activity?.let { activity ->
                        val pm: PackageManager = activity.packageManager
                        val packageInfo = pm.getPackageInfo(activity.packageName, 0)
                        val resolveInfoIntent = Intent(Intent.ACTION_MAIN)
                        resolveInfoIntent.setPackage(packageInfo.packageName)
                        resolveInfoIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

                        val apps = pm.queryIntentActivities(resolveInfoIntent, 0)
                        apps.getOrNull(0)?.let { app ->
                            val activityInfo = app.activityInfo
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.component = ComponentName(
                                activityInfo.applicationInfo.packageName,
                                activityInfo.name
                            )
                            activity.startActivity(intent)
                        }
                    }
                }
            }
        }
        activity?.finish()
    }

    companion object {
        fun getInstance(bundle: Bundle?) = LangSelectionFragment().apply { arguments = bundle }

        const val ACTION_ID_CANCEL = -1L
    }
}
