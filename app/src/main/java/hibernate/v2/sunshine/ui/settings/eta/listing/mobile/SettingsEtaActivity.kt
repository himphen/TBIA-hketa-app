package hibernate.v2.sunshine.ui.settings.eta.listing.mobile

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class SettingsEtaActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = SettingsEtaFragment.getInstance()
    override var titleId: Int? = R.string.title_activity_settings_eta
}