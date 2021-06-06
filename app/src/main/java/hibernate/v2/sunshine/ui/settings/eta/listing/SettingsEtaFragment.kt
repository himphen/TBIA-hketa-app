package hibernate.v2.sunshine.ui.settings.eta.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.RemoveEta
import hibernate.v2.sunshine.model.RouteStopList
import hibernate.v2.sunshine.repository.RouteStopListDataHolder
import hibernate.v2.sunshine.ui.settings.eta.SettingsEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaActivity
import hibernate.v2.sunshine.ui.settings.eta.remove.RemoveEtaDialogActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsEtaFragment : VerticalGridSupportFragment() {

    var addEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            updateRows()
        }
    }

    var removeEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch(Dispatchers.IO) {
                val etaEntity = result.data?.getParcelableExtra<EtaEntity>(
                    RemoveEtaDialogActivity.ARG_REMOVE_ETA_OUTGOING
                )

                val position =
                    result.data?.getIntExtra(RemoveEtaDialogActivity.ARG_REMOVE_ETA_OUTGOING, -1)

                if (etaEntity != null && position != null && position != -1) {
                    viewModel.clearData(etaEntity)
                    mAdapter?.removeItems(position, 1)
                }
            }
        }
    }

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE

        fun getInstance() = SettingsEtaFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<SettingsEtaViewModel>()

    init {
        lifecycleScope.launch {
            launch {
                viewModel.routeAndStopListReady.observe(this@SettingsEtaFragment, {
                    if (it) {
                        updateRows()
                    }
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_activity_settings_eta)
        setupRowAdapter()
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = COLUMNS
        setGridPresenter(gridPresenter)
        val cardPresenter = SettingsEtaCardPresenter(requireContext(),
            object : SettingsEtaCardPresenter.ClickListener {
                override fun onItemClick(card: Card.SettingsEtaCard) {
                    when (card.type) {
                        Card.SettingsEtaCard.Type.INSERT_ROW -> {
                            viewModel.routeStopListHashMap.value?.values?.let {
                                // TODO update performance
                                val list = arrayListOf<RouteStopList>().apply { addAll(it) }
                                list.sortWith { o1, o2 ->
                                    o1.route.compareTo(o2.route)
                                }
                                RouteStopListDataHolder.data = list
                                addEtaLauncher.launch(Intent(context, AddEtaActivity::class.java))
                            }
                        }
                        Card.SettingsEtaCard.Type.DATA -> {
                            removeEtaLauncher.launch(
                                Intent(
                                    context,
                                    RemoveEtaDialogActivity::class.java
                                ).apply {
                                    putExtra(
                                        RemoveEtaDialogActivity.ARG_REMOVE_ETA_INCOMING,
                                        RemoveEta(
                                            entity = card.entity!!,
                                            route = card.route!!,
                                            stop = card.stop!!
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
        prepareEntranceTransition()
        Handler(Looper.getMainLooper()).postDelayed({
            startEntranceTransition()
        }, 1000)

        viewModel.getAllRouteAndStopList()
    }

    private fun updateRows() {
        lifecycleScope.launch(Dispatchers.Main) {
            val savedRouteStopList = viewModel.getData()

            val list = savedRouteStopList.mapNotNull { etaEntity ->
                val route = viewModel.routeHashMap.value?.get(etaEntity.routeHashId())
                val stop = viewModel.stopHashMap.value?.get(etaEntity.stopId)

                if (route == null || stop == null) return@mapNotNull null

                return@mapNotNull Card.SettingsEtaCard(
                    entity = etaEntity,
                    route = route,
                    stop = stop,
                    type = Card.SettingsEtaCard.Type.DATA
                )
            }.toMutableList()

            list.add(
                0,
                Card.SettingsEtaCard(
                    entity = null,
                    route = null,
                    stop = null,
                    type = Card.SettingsEtaCard.Type.INSERT_ROW
                )
            )

            mAdapter?.addAll(0, list)
        }
    }
}