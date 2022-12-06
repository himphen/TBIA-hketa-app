package hibernate.v2.tbia.ui.settings.lang.leanback

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment

class LangSelectionActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = LangSelectionFragment.getInstance(null)
        GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
    }
}
