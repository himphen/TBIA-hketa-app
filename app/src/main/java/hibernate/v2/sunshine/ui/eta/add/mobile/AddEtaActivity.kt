package hibernate.v2.sunshine.ui.eta.add.mobile

import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.ui.eta.add.AddEtaMobileViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class AddEtaActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = AddEtaViewPagerFragment()
    override var titleId: Int? = R.string.title_activity_add_eta

    val viewModel by stateViewModel<AddEtaMobileViewModel>()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (!popBackStack()) {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (!popBackStack()) {
            super.onBackPressed()
        }
    }
}
