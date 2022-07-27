package hibernate.v2.sunshine.ui.bookmark.edit.leanback

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.BookmarkEdit

class BookmarkEditConfirmDialogFragment : GuidedStepSupportFragment() {
    private lateinit var currentBookmarkEdit: BookmarkEdit
    private var beforeId: String? = null
    private var afterId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        currentBookmarkEdit =
            arguments?.getParcelable(BookmarkEditConfirmDialogActivity.ARG_SELECTED_ETA)!!
        beforeId = arguments?.getString(BookmarkEditConfirmDialogActivity.ARG_BEFORE_ETA_ID)
        afterId = arguments?.getString(BookmarkEditConfirmDialogActivity.ARG_AFTER_ETA_ID)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_edit_eta_title,
                currentBookmarkEdit.route.routeNo,
                currentBookmarkEdit.stop.nameTc
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
                .id(ACTION_ID_MOVE_UP)
                .title(getString(R.string.dialog_edit_eta_move_up_btn)).build()
            actions.add(action)
        }

        if (afterId != null) {
            action = GuidedAction.Builder(context)
                .id(ACTION_ID_MOVE_DOWN)
                .title(getString(R.string.dialog_edit_eta_move_down_btn)).build()
            actions.add(action)
        }

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_REMOVE)
            .title(getString(R.string.dialog_edit_eta_remove_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CANCEL)
            .title(getString(R.string.dialog_edit_eta_cancel_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_MOVE_UP,
            ACTION_ID_MOVE_DOWN,
            ACTION_ID_REMOVE -> {
                activity?.setResult(
                    Activity.RESULT_OK,
                    Intent().apply {
                        putExtra(BookmarkEditConfirmDialogActivity.ARG_RESULT_CODE, action.id)
                    }
                )
            }
        }
        activity?.finish()
    }

    companion object {
        fun getInstance(bundle: Bundle?) =
            BookmarkEditConfirmDialogFragment().apply { arguments = bundle }

        const val ACTION_ID_MOVE_UP = 1L
        const val ACTION_ID_MOVE_DOWN = 2L
        const val ACTION_ID_REMOVE = 3L
        const val ACTION_ID_CANCEL = 4L
    }
}
