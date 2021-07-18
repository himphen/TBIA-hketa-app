package hibernate.v2.sunshine.ui.settings.eta.add.leanback

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.LbActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity

class AddEtaActivity : BaseFragmentActivity<LbActivityContainerBinding>() {
    override var fragment: Fragment? = AddEtaBrandSelectionFragment.getInstance()

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