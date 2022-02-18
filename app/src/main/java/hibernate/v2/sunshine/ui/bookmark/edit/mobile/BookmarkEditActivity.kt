package hibernate.v2.sunshine.ui.bookmark.edit.mobile

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkEditActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = BookmarkEditFragment.getInstance()
    override var titleId: Int? = R.string.title_activity_settings_eta

    override fun onBackPressed() {
        (fragment as BookmarkEditFragment).apply {
            lifecycleScope.launch(Dispatchers.Main) {
                checkSaveNewEtaOrder()
                super.onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
