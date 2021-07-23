package hibernate.v2.sunshine.ui.eta.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.himphen.logger.Logger
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.databinding.FragmentEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.TransportEta
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.ui.eta.leanback.EtaCardPresenter
import hibernate.v2.sunshine.util.DateUtil
import hibernate.v2.sunshine.util.dpToPx
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
        initAdapter()
    }

    private fun updateRouteEtaStopList() {
        if (refreshEtaJob == null) {
            refreshEtaJob = CoroutineScope(Dispatchers.Main).launchPeriodicAsync(REFRESH_TIME) {
                viewModel.updateEtaList(etaCardList)
            }
        }
    }

    private fun processEtaList() {
        val etaCardList = etaCardList
        if (etaCardList.isNullOrEmpty()) {
            viewBinding?.emptyViewCl?.root?.visibility = View.VISIBLE
            viewBinding?.emptyViewCl?.emptyDescTv?.text = getString(R.string.empty_eta_list)
        } else {
            viewBinding?.emptyViewCl?.root?.visibility = View.GONE
            etaCardList.forEachIndexed { index, etaCard ->
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
    }

    fun initAdapter() {
        Logger.d("updateAdapterViewType")
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = null
        viewBinding.recyclerView.adapter = adapter
        adapter.type = sharedPreferencesManager.etaCardType

        when (adapter.type) {
            EtaCardPresenter.CardViewType.Classic -> {
                context?.let { context ->
                    val dividerItemDecoration =
                        DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                    viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
                }

                viewBinding.recyclerView.apply {
                    setPadding(dpToPx(0), dpToPx(0), dpToPx(0), dpToPx(0))
                }
            }
            EtaCardPresenter.CardViewType.Standard,
            EtaCardPresenter.CardViewType.Compact -> {
                viewBinding.recyclerView.apply {
                    while (itemDecorationCount > 0) {
                        removeItemDecorationAt(0)
                    }

                    setPadding(dpToPx(0), dpToPx(4), dpToPx(0), dpToPx(4))
                }
            }
        }
    }

    fun updateAdapterData() {
        Logger.d("updateAdapterData")
        etaCardList = null
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