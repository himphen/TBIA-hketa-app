package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class AddEtaActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = AddEtaFragment.getInstance()
    override var titleId: Int? = R.string.title_activity_add_eta

    val viewModel by stateViewModel<AddEtaViewModel>()

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