package hibernate.v2.sunshine.ui.settings.eta.layout

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogFragment

class EtaLayoutDialogActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = EtaLayoutDialogFragment.getInstance(null)
        GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
    }
}