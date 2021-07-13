package hibernate.v2.sunshine.ui.settings.eta.layout

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.ui.eta.view.EtaCardPresenter
import org.koin.android.ext.android.inject

class EtaLayoutDialogFragment : GuidedStepSupportFragment() {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_eta_layout_title
            ),
            "",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        var action: GuidedAction = GuidedAction.Builder(context)
            .id(ACTION_ID_STANDARD)
            .title(getString(R.string.dialog_eta_layout_standard_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_COMPACT)
            .title(getString(R.string.dialog_eta_layout_compact_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CLASSIC)
            .title(getString(R.string.dialog_eta_layout_classic_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CANCEL)
            .title(getString(R.string.dialog_eta_layout_cancel_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_STANDARD,
            ACTION_ID_COMPACT,
            ACTION_ID_CLASSIC -> {
                sharedPreferencesManager.etaCardType =
                    EtaCardPresenter.CardViewType.from(action.id.toInt())
            }
        }
        activity?.finish()
    }

    companion object {
        fun getInstance(bundle: Bundle?) = EtaLayoutDialogFragment().apply { arguments = bundle }

        val ACTION_ID_STANDARD = EtaCardPresenter.CardViewType.Standard.value.toLong()
        val ACTION_ID_COMPACT = EtaCardPresenter.CardViewType.Compact.value.toLong()
        val ACTION_ID_CLASSIC = EtaCardPresenter.CardViewType.Classic.value.toLong()
        const val ACTION_ID_CANCEL = -1L
    }
}