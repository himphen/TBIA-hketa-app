package hibernate.v2.tbia.ui.settings.eta.layout.mobile

import androidx.fragment.app.Fragment
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.ActivityContainerBinding
import hibernate.v2.tbia.ui.base.BaseFragmentActivity

class EtaLayoutSelectionActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = EtaLayoutSelectionFragment.getInstance()
    override var titleId: Int? = R.string.title_activity_eta_layout
}
