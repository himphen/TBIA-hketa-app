package hibernate.v2.sunshine.ui.settings.eta.layout.mobile

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class EtaLayoutSelectionActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = EtaLayoutSelectionFragment.getInstance()
    override var titleId: Int? = R.string.title_activity_eta_layout
}
