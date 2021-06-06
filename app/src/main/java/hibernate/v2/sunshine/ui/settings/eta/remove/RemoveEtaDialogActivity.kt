package hibernate.v2.sunshine.ui.settings.eta.remove

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import hibernate.v2.sunshine.model.RemoveEta

class RemoveEtaDialogActivity : FragmentActivity() {

    companion object {
        const val ARG_REMOVE_ETA_INCOMING = "ARG_REMOVE_ETA_INCOMING"
        const val ARG_REMOVE_ETA_OUTGOING = "ARG_REMOVE_ETA_OUTGOING"
        const val ARG_REMOVE_ETA_POSITION = "ARG_REMOVE_ETA_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val removeEta = intent?.getParcelableExtra<RemoveEta>(ARG_REMOVE_ETA_INCOMING) ?: run {
            finish()
            return
        }

        val removePosition = intent?.getIntExtra(ARG_REMOVE_ETA_POSITION, 0) ?: 0

        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#21272A")))
        if (savedInstanceState == null) {
            val fragment = RemoveEtaDialogFragment.getInstance(removeEta, removePosition)
            GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
        }
    }
}