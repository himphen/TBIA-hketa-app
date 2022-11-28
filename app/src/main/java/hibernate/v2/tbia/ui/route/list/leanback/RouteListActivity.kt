package hibernate.v2.tbia.ui.route.list.leanback

import androidx.fragment.app.Fragment
import hibernate.v2.tbia.databinding.LbActivityContainerBinding
import hibernate.v2.tbia.ui.base.BaseLeanbackActivity

class RouteListActivity : BaseLeanbackActivity<LbActivityContainerBinding>() {
    override var fragment: Fragment? = RouteListBrandSelectionFragment.getInstance()

    override fun getActivityViewBinding() =
        LbActivityContainerBinding.inflate(layoutInflater)

    override fun onBackPressed() {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val ARG_ETA_TYPE = "ARG_ETA_TYPE"
    }
}
