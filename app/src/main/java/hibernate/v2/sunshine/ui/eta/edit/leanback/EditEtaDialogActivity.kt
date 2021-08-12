package hibernate.v2.sunshine.ui.eta.edit.leanback

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import hibernate.v2.sunshine.model.EditEta

class EditEtaDialogActivity : FragmentActivity() {

    companion object {
        const val ARG_BUNDLE = "ARG_BUNDLE"
        const val ARG_SELECTED_ETA = "ARG_SELECTED_ETA"
        const val ARG_BEFORE_ETA_ID = "ARG_BEFORE_ETA_ID"
        const val ARG_AFTER_ETA_ID = "ARG_AFTER_ETA_ID"
        const val ARG_RESULT_CODE = "ARG_RESULT_CODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.getBundleExtra(ARG_BUNDLE)?.getParcelable<EditEta>(ARG_SELECTED_ETA) ?: run {
            finish()
            return
        }

        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#21272A")))
        if (savedInstanceState == null) {
            val fragment = EditEtaDialogFragment.getInstance(intent?.getBundleExtra(ARG_BUNDLE))
            GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
        }
    }
}