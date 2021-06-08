package hibernate.v2.sunshine.ui.settings.eta.add

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ObjectAdapter
import androidx.lifecycle.lifecycleScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.RouteStopListDataHolder
import hibernate.v2.sunshine.ui.settings.eta.SettingsEtaViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddEtaFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val viewModel by inject<SettingsEtaViewModel>()

    private var mQuery: String = ""
    private var mResultsFound = false

    private val clickListener = object : AddEtaCardPresenter.ClickListener {
        override fun onItemClick(card: Card.RouteStopCard) {
            lifecycleScope.launch {
                val data = viewModel.getEtaList(
                    stopId = card.stop.stopId,
                    routeId = card.route.routeId,
                    bound = card.route.bound,
                    serviceType = card.route.serviceType,
                    seq = card.stop.seq!!
                )

                if (data.isNotEmpty()) {
                    Toast.makeText(context, "這個已經加入", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val newEta = EtaEntity(
                    stopId = card.stop.stopId,
                    routeId = card.route.routeId,
                    bound = card.route.bound,
                    serviceType = card.route.serviceType,
                    seq = card.stop.seq!!
                )
                viewModel.addEta(newEta)

                val currentEtaOrderList = viewModel.getEtaOrderList()
                val updatedEtaOrderList = mutableListOf<EtaOrderEntity>()
                updatedEtaOrderList.add(EtaOrderEntity(id = newEta.id, position = 0))
                updatedEtaOrderList.addAll(currentEtaOrderList.map {
                    EtaOrderEntity(id = it.id, position = it.position + 1)
                })
                viewModel.updateEtaOrderList(updatedEtaOrderList)

                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }
        }
    }

    companion object {
        fun getInstance() = AddEtaFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "加入"

        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        loadRows()
        setSearchResultProvider(this)
    }

    private fun loadRows() {
        RouteStopListDataHolder.data?.let { list ->
            list.forEachIndexed { index, routeStopList ->
                // Init Title
                val route = routeStopList.route
                val headerTitle = if (route.isSpecialRoute()) {
                    "${route.routeId} 特別線 (${route.serviceType}) - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
                } else {
                    "${route.routeId} - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
                }

                if (mQuery.isNotEmpty() && !headerTitle.contains(mQuery)) return@forEachIndexed

                // Init Row
                val listRowAdapter =
                    ArrayObjectAdapter(AddEtaCardPresenter(requireContext(), clickListener))
                listRowAdapter.addAll(0, routeStopList.stopList.map {
                    Card.RouteStopCard(
                        route = routeStopList.route,
                        stop = it
                    )
                })
                val header = HeaderItem(index.toLong(), headerTitle)
                val listRow = ListRow(header, listRowAdapter)
                mRowsAdapter.add(listRow)
            }
        }
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return mRowsAdapter
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        Logger.d(String.format("Search text changed: %s", newQuery))
//        loadQuery(newQuery)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Logger.d(String.format("Search text submitted: %s", query))
        loadQuery(query)
        return true
    }

    private fun loadQuery(query: String) {
        query.trim().let {
            if (it != mQuery) {
                mQuery = query
                loadRows()
            }
        }
    }
}