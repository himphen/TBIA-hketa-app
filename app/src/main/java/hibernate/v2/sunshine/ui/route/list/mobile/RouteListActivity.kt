package hibernate.v2.sunshine.ui.route.list.mobile

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import hibernate.v2.utils.colorInt
import hibernate.v2.utils.localized
import org.koin.androidx.viewmodel.ext.android.viewModel

class RouteListActivity : BaseFragmentActivity<ActivityContainerBinding>() {

    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = RouteListViewPagerFragment()
    override var titleId: Int? = R.string.title_activity_add_eta

    private val viewModel by viewModel<RouteListMobileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initEvent()
    }

    private fun initEvent() {
        viewModel.tabItemSelectedLiveData.observe(this) { item ->
            val color = item.color().colorInt(this)

            window?.statusBarColor = color

            supportActionBar?.let {
                it.title = getString(
                    R.string.title_activity_add_eta_with_company_name,
                    item.name().localized(this)
                )
            }

            viewBinding.appBarLayout.elevation = 0f
            viewBinding.toolbar.root.setBackgroundColor(color)
        }
    }

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
