package hibernate.v2.sunshine.ui.settings.eta.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.EditEta

class EditEtaDialogFragment : GuidedStepSupportFragment() {
    private lateinit var currentEditEta: EditEta
    private var beforeId: String? = null
    private var afterId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentEditEta = arguments?.getParcelable(EditEtaDialogActivity.ARG_SELECTED_ETA)!!
        beforeId = arguments?.getString(EditEtaDialogActivity.ARG_BEFORE_ETA_ID)
        afterId = arguments?.getString(EditEtaDialogActivity.ARG_AFTER_ETA_ID)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_edit_eta_title,
                currentEditEta.route.routeId,
                currentEditEta.stop.nameTc
            ),
            getString(R.string.dialog_edit_eta_description),
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        var action: GuidedAction

        if (beforeId != null) {
            action = GuidedAction.Builder(context)
                .id(ACTION_ID_MOVE_UP.toLong())
                .title(getString(R.string.dialog_edit_eta_move_up_btn)).build()
            actions.add(action)
        }

        if (afterId != null) {
            action = GuidedAction.Builder(context)
                .id(ACTION_ID_MOVE_DOWN.toLong())
                .title(getString(R.string.dialog_edit_eta_move_down_btn)).build()
            actions.add(action)
        }

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_REMOVE.toLong())
            .title(getString(R.string.dialog_edit_eta_remove_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CANCEL.toLong())
            .title(getString(R.string.dialog_edit_eta_cancel_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_MOVE_UP.toLong(),
            ACTION_ID_MOVE_DOWN.toLong(),
            ACTION_ID_REMOVE.toLong() -> {
                activity?.setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(EditEtaDialogActivity.ARG_RESULT_CODE, action.id)
                })
            }
        }
        activity?.finish()
    }

    companion object {
        fun getInstance(bundle: Bundle?) = EditEtaDialogFragment().apply { arguments = bundle }

        const val ACTION_ID_MOVE_UP = 1
        const val ACTION_ID_MOVE_DOWN = 2
        const val ACTION_ID_REMOVE = 3
        const val ACTION_ID_CANCEL = 4
    }
}