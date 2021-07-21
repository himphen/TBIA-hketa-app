package hibernate.v2.sunshine.ui.settings.eta.layout.leanback

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment

class EtaLayoutSelectionActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = EtaLayoutSelectionFragment.getInstance(null)
        GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
    }
}