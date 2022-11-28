package hibernate.v2.tbia.ui.settings.eta.layout.leanback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.model.transport.card.EtaCardViewType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.visible
import org.koin.android.ext.android.inject

class EtaLayoutSelectionFragment : GuidedStepSupportFragment() {

    private lateinit var etaDemoClassic: ConstraintLayout
    private lateinit var etaDemoCompact: ConstraintLayout
    private lateinit var etaDemoStandard: ConstraintLayout
    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override fun onCreateGuidanceStylist(): GuidanceStylist {
        return object : GuidanceStylist() {
            override fun onProvideLayoutId(): Int {
                return R.layout.lb_guidance_eta_layout
            }

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup,
                guidance: Guidance?
            ): View {
                val view = super.onCreateView(inflater, container, guidance)
                etaDemoClassic = view.findViewById(R.id.eta_demo_classic)
                etaDemoCompact = view.findViewById(R.id.eta_demo_compact)
                etaDemoStandard = view.findViewById(R.id.eta_demo_standard)
                etaDemoStandard.visible()

                view.findViewById<View>(R.id.routeCompanyColor).visible()
                return view
            }
        }
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(
                R.string.dialog_eta_layout_title
            ),
            "",
            "",
            null
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        var action: GuidedAction = GuidedAction.Builder(context)
            .id(ACTION_ID_STANDARD)
            .title(getString(R.string.dialog_eta_layout_standard_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_COMPACT)
            .title(getString(R.string.dialog_eta_layout_compact_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CLASSIC)
            .title(getString(R.string.dialog_eta_layout_classic_btn)).build()
        actions.add(action)

        action = GuidedAction.Builder(context)
            .id(ACTION_ID_CANCEL)
            .title(getString(R.string.dialog_eta_layout_cancel_btn)).build()
        actions.add(action)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_STANDARD,
            ACTION_ID_COMPACT,
            ACTION_ID_CLASSIC -> {
                sharedPreferencesManager.etaCardType =
                    EtaCardViewType.from(action.id.toInt())
            }
        }
        activity?.finish()
    }

    override fun onGuidedActionFocused(action: GuidedAction) {
        when (action.id) {
            ACTION_ID_STANDARD -> {
                etaDemoClassic.gone()
                etaDemoCompact.gone()
                etaDemoStandard.visible()
            }
            ACTION_ID_COMPACT -> {
                etaDemoClassic.gone()
                etaDemoCompact.visible()
                etaDemoStandard.gone()
            }
            ACTION_ID_CLASSIC -> {
                etaDemoClassic.visible()
                etaDemoCompact.gone()
                etaDemoStandard.gone()
            }
            ACTION_ID_CANCEL -> {
                etaDemoClassic.gone()
                etaDemoCompact.gone()
                etaDemoStandard.gone()
            }
        }
    }

    companion object {
        fun getInstance(bundle: Bundle?) = EtaLayoutSelectionFragment().apply { arguments = bundle }

        val ACTION_ID_STANDARD = EtaCardViewType.Standard.value.toLong()
        val ACTION_ID_COMPACT = EtaCardViewType.Compact.value.toLong()
        val ACTION_ID_CLASSIC = EtaCardViewType.Classic.value.toLong()
        const val ACTION_ID_CANCEL = -1L
    }
}
