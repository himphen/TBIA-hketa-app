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
import org.koin.android.ext.android.inject

class EtaLayoutSelectionFragment : BaseFragment<FragmentEtaLayoutSelectionBinding>() {

    private lateinit var etaDemoClassic: FrameLayout
    private lateinit var etaDemoCompact: CardView
    private lateinit var etaDemoStandard: CardView

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val adapter = EtaLayoutAdapter(sharedPreferencesManager.etaCardType,
        object : EtaLayoutAdapter.ItemListener {
            override fun onItemSelected(item: EtaCardViewType) {
                sharedPreferencesManager.etaCardType = item
                updateDemoLayout(item)
                activity?.setResult(Activity.RESULT_OK)
            }
        }
    )

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
                etaDemoClassic.visibility = View.GONE
                etaDemoCompact.visibility = View.GONE
                etaDemoStandard.visibility = View.VISIBLE
            }
            EtaCardViewType.Compact -> {
                etaDemoClassic.visibility = View.GONE
                etaDemoCompact.visibility = View.VISIBLE
                etaDemoStandard.visibility = View.GONE
            }
            EtaCardViewType.Classic -> {
                etaDemoClassic.visibility = View.VISIBLE
                etaDemoCompact.visibility = View.GONE
                etaDemoStandard.visibility = View.GONE
            }
        }
    }

    companion object {
        fun getInstance() = EtaLayoutSelectionFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEtaLayoutSelectionBinding.inflate(inflater, container, false)
}