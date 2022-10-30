package hibernate.v2.sunshine.ui.route.details.mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.TransportRoute
import hibernate.v2.sunshine.databinding.ActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseFragmentActivity
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.parameter.parametersOf

class RouteDetailsActivity : BaseFragmentActivity<ActivityContainerBinding>() {
    override fun getActivityViewBinding() = ActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = RouteDetailsFragment()

    lateinit var viewModel: RouteDetailsMobileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val route = intent.getParcelableExtra<TransportRoute>(ARG_SELECTED_ROUTE)
        viewModel = getStateViewModel {
            parametersOf(
                route,
                intent.getSerializableExtra(ARG_SELECTED_ETA_TYPE) as EtaType
            )
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

    companion object {
        private const val ARG_SELECTED_ROUTE = "ARG_SELECTED_ROUTE"
        private const val ARG_SELECTED_ETA_TYPE = "ARG_SELECTED_ETA_TYPE"

        fun launch(
            context: Context?,
            selectedRoute: TransportRoute,
            selectedEtaType: EtaType
        ) {
            val intent = getLaunchIntent(context, selectedRoute, selectedEtaType)
            context?.startActivity(intent)
        }

        fun getLaunchIntent(
            context: Context?,
            selectedRoute: TransportRoute,
            selectedEtaType: EtaType
        ) = Intent(context, RouteDetailsActivity::class.java).apply {
            putExtra(ARG_SELECTED_ROUTE, selectedRoute)
            putExtra(ARG_SELECTED_ETA_TYPE, selectedEtaType)
        }
    }
}
