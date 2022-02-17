package hibernate.v2.sunshine.ui.route.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.TransportRoute
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.parameter.parametersOf

class RouteDetailsActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = RouteDetailsFragment()
//    override var titleId: Int? = R.string.title_activity_add_eta

    lateinit var viewModel: RouteDetailsMobileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getStateViewModel {
            parametersOf(
                intent.getParcelableExtra(ARG_SELECTED_ROUTE),
                intent.getSerializableExtra(ARG_SELECTED_ETA_TYPE) as EtaType
            )
        }
    }

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

    companion object {
        private const val ARG_SELECTED_ROUTE = "ARG_SELECTED_ROUTE"
        private const val ARG_SELECTED_ETA_TYPE = "ARG_SELECTED_ETA_TYPE"

        fun launch(
            context: Context?,
            selectedRoute: TransportRoute,
            selectedEtaType: EtaType
        ) {
            val intent = Intent(context, RouteDetailsActivity::class.java).apply {
                putExtra(ARG_SELECTED_ROUTE, selectedRoute)
                putExtra(ARG_SELECTED_ETA_TYPE, selectedEtaType)
            }
            context?.startActivity(intent)
        }
    }
}
