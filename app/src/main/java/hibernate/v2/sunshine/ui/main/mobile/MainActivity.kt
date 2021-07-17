package hibernate.v2.sunshine.ui.main.mobile

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class MainActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = MainFragment()
    override var titleId: Int? = R.string.title_fragment_home
}