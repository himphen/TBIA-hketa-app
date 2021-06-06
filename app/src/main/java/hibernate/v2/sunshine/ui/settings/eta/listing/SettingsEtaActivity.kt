package hibernate.v2.sunshine.ui.settings.eta.listing

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.ActivityBaseContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class SettingsEtaActivity : BaseFragmentActivity<ActivityBaseContainerBinding>() {
    override var fragment: Fragment? = SettingsEtaFragment.getInstance()

    override fun getActivityViewBinding() =
        ActivityBaseContainerBinding.inflate(layoutInflater)
}