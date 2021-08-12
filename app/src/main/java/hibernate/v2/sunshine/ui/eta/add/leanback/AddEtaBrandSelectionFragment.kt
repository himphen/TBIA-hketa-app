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
        var action = GuidedAction.Builder(context)
            .id(ACTION_ID_KMB)
            .title(getString(R.string.dialog_add_eta_brand_selection_kmb_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_NWFB)
            .title(getString(R.string.dialog_add_eta_brand_selection_nwfb_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_NWFB)
            .title(getString(R.string.dialog_add_eta_brand_selection_ctb_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_GMB_HKI)
            .title(getString(R.string.dialog_add_eta_brand_selection_gmb_hki_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_GMB_KLN)
            .title(getString(R.string.dialog_add_eta_brand_selection_gmb_kln_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_GMB_NT)
            .title(getString(R.string.dialog_add_eta_brand_selection_gmb_nt_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val etaType = when (action.id) {
            ACTION_ID_KMB -> EtaType.KMB
            ACTION_ID_NWFB -> EtaType.NWFB
            ACTION_ID_CTB -> EtaType.CTB
            ACTION_ID_GMB_HKI -> EtaType.GMB_HKI
            ACTION_ID_GMB_KLN -> EtaType.GMB_KLN
            ACTION_ID_GMB_NT -> EtaType.GMB_NT
            else -> return
        }

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

        const val ACTION_ID_KMB = 1L
        const val ACTION_ID_NWFB = 2L
        const val ACTION_ID_CTB = 3L
        const val ACTION_ID_GMB_HKI = 4L
        const val ACTION_ID_GMB_KLN = 5L
        const val ACTION_ID_GMB_NT = 6L
    }
}