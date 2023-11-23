package hibernate.v2.tbia.ui.route.list.leanback

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.ui.route.list.leanback.RouteListActivity.Companion.ARG_ETA_TYPE
import hibernate.v2.tbia.util.putEnum
import hibernate.v2.utils.localized

class RouteListBrandSelectionFragment : GuidedStepSupportFragment() {

    val list = listOf(
        EtaType.KMB,
        EtaType.CTB,
        EtaType.GMB_HKI,
        EtaType.GMB_KLN,
        EtaType.GMB_NT,
        EtaType.MTR,
        EtaType.LRT,
    )

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.add_eta_brand_selection_title
            ),
            "",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        list.forEachIndexed { index, etaType ->
            val action = GuidedAction.Builder(context)
                .id(index.toLong())
                .title(etaType.name().localized(requireContext())).build()
            actions.add(action)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val etaType = list.getOrNull(action.id.toInt()) ?: return

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(
                R.id.container,
                RouteListFragment.getInstance(
                    Bundle().apply {
                        putEnum(ARG_ETA_TYPE, etaType)
                    }
                )
            )
            commit()
        }
    }

    companion object {
        fun getInstance() = RouteListBrandSelectionFragment()
    }
}
