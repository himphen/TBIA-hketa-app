package hibernate.v2.sunshine.ui.eta.add.leanback

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.eta.add.leanback.AddEtaActivity.Companion.ARG_ETA_TYPE
import hibernate.v2.sunshine.util.putEnum

class AddEtaBrandSelectionFragment : GuidedStepSupportFragment() {

    val list = listOf(
        EtaType.KMB,
        EtaType.NWFB,
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
                R.string.dialog_add_eta_brand_selection_title
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
                .title(etaType.name(requireContext())).build()
            actions.add(action)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val etaType = list.getOrNull(action.id.toInt()) ?: return

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(
                R.id.container,
                AddEtaFragment.getInstance(Bundle().apply {
                    putEnum(ARG_ETA_TYPE, etaType)
                })
            )
            commit()
        }
    }

    companion object {
        fun getInstance() = AddEtaBrandSelectionFragment()
    }
}