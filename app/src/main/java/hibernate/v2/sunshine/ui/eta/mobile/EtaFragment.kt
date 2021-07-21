package hibernate.v2.sunshine.ui.eta.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.launchPeriodicAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Date

class EtaFragment : BaseFragment<FragmentEtaBinding>() {

    companion object {
        private const val REFRESH_TIME = 60 * 1000L
        fun getInstance() = EtaFragment()
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    private val adapter = EtaCardAdapter(sharedPreferencesManager.etaCardType)

    private val viewModel by inject<EtaViewModel>()
    private var etaCardList: MutableList<Card.EtaCard>? = null
    private var refreshEtaJob: Deferred<Unit>? = null

    override fun onResume() {
        super.onResume()
        updateRouteEtaStopList()
    }

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            etaCardList?.let { etaCardList ->
                etaCardList.clear()
                etaCardList.addAll(it)
                processEtaList()
            } ?: run {
                etaCardList = it.toMutableList()
                adapter.setData(etaCardList)
                processEtaList()

                viewModel.updateEtaList(etaCardList)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initUi()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.getEtaListFromDb()
        }
    }

    private fun initUi() {
        viewBinding!!.recyclerView.adapter = adapter
    }

    private fun updateRouteEtaStopList() {
        if (refreshEtaJob == null) {
            refreshEtaJob = CoroutineScope(Dispatchers.Main).launchPeriodicAsync(REFRESH_TIME) {
                viewModel.updateEtaList(etaCardList)
            }
        }
    }

    private fun processEtaList() {
        etaCardList?.forEachIndexed { index, etaCard ->
            etaCard.etaList = etaCard.etaList.filter { eta: TransportEta ->
                eta.eta?.let { etaDate ->
                    val currentDate = Date()
                    DateUtil.getTimeDiffInMin(
                        etaDate,
                        currentDate
                    ) > 0
                } ?: run {
                    false
                }
            }

            adapter.replace(index, etaCard)
        }
    }

    fun updateAdapterViewType() {
        adapter.type = sharedPreferencesManager.etaCardType
        updateAdapterData()
    }

    fun updateAdapterData() {
        initData()
    }

    override fun onPause() {
        super.onPause()
        refreshEtaJob?.cancel()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEtaBinding.inflate(inflater, container, false)
}