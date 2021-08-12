package hibernate.v2.sunshine.ui.settings.eta.layout.mobile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentEtaLayoutSelectionBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.EtaCardViewType
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.visible
import org.koin.android.ext.android.inject

class EtaLayoutSelectionFragment : BaseFragment<FragmentEtaLayoutSelectionBinding>() {

    private lateinit var etaDemoClassic: FrameLayout
    private lateinit var etaDemoCompact: CardView
    private lateinit var etaDemoStandard: CardView

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val adapter =
        EtaLayoutAdapter(sharedPreferencesManager.etaCardType) { item: EtaCardViewType ->
            sharedPreferencesManager.etaCardType = item
            updateDemoLayout(item)
            activity?.setResult(Activity.RESULT_OK)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!

        etaDemoClassic = viewBinding.etaDemoClassic
        etaDemoCompact = viewBinding.etaDemoCompact
        etaDemoStandard = viewBinding.etaDemoStandard

        viewBinding.recyclerView.adapter = adapter
        updateDemoLayout(sharedPreferencesManager.etaCardType)
    }

    fun updateDemoLayout(type: EtaCardViewType) {
        when (type) {
            EtaCardViewType.Standard -> {
                etaDemoClassic.gone()
                etaDemoCompact.gone()
                etaDemoStandard.visible()
            }
            EtaCardViewType.Compact -> {
                etaDemoClassic.gone()
                etaDemoCompact.visible()
                etaDemoStandard.gone()
            }
            EtaCardViewType.Classic -> {
                etaDemoClassic.visible()
                etaDemoCompact.gone()
                etaDemoStandard.gone()
            }
        }
    }

    companion object {
        fun getInstance() = EtaLayoutSelectionFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentEtaLayoutSelectionBinding.inflate(inflater, container, false)
}