package hibernate.v2.sunshine.ui.settings.eta.add

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.ActivityBaseContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class AddEtaActivity : BaseFragmentActivity<ActivityBaseContainerBinding>() {
    override var fragment: Fragment? = AddEtaFragment.getInstance()

    override fun getActivityViewBinding() =
        ActivityBaseContainerBinding.inflate(layoutInflater)

}