package hibernate.v2.sunshine.ui.route.list.mobile

import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class RouteListActivity : BaseFragmentActivity<ActivityContainerBinding>() {

    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = RouteListViewPagerFragment()
    override var titleId: Int? = R.string.title_activity_add_eta

    val viewModel by stateViewModel<RouteListMobileViewModel>()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
