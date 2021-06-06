package hibernate.v2.sunshine.ui.settings.eta.remove

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.RemoveEta

class RemoveEtaDialogFragment : GuidedStepSupportFragment() {
    private lateinit var removeEta: RemoveEta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        removeEta = arguments?.getParcelable(RemoveEtaDialogActivity.ARG_REMOVE_ETA_INCOMING)!!
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_remove_eta_title,
                removeEta.route.routeId,
                removeEta.stop.nameTc
            ),
            getString(R.string.dialog_remove_eta_description),
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        var action = GuidedAction.Builder(context)
            .id(ACTION_ID_POSITIVE.toLong())
            .title(getString(R.string.dialog_remove_eta_pos_btn)).build()
        actions.add(action)
        action = GuidedAction.Builder(context)
            .id(ACTION_ID_NEGATIVE.toLong())
            .title(getString(R.string.dialog_remove_eta_neg_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (ACTION_ID_POSITIVE.toLong() == action.id) {
            activity?.setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    putExtra(RemoveEtaDialogActivity.ARG_REMOVE_ETA_OUTGOING, removeEta.entity)
                    putExtra(
                        RemoveEtaDialogActivity.ARG_REMOVE_ETA_POSITION,
                        arguments?.getInt(RemoveEtaDialogActivity.ARG_REMOVE_ETA_INCOMING) ?: -1
                    )
                }
            )
        }
        activity?.finish()
    }

    companion object {
        fun getInstance(removeEta: RemoveEta, removePosition: Int) =
            RemoveEtaDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RemoveEtaDialogActivity.ARG_REMOVE_ETA_INCOMING, removeEta)
                    putInt(RemoveEtaDialogActivity.ARG_REMOVE_ETA_POSITION, removePosition)
                }
            }

        private const val ACTION_ID_POSITIVE = 1
        private const val ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1
    }
}