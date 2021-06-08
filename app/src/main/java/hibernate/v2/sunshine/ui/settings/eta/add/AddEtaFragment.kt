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
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddEtaFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val viewModel by inject<AddEtaViewModel>()

    private var mQuery: String = ""

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
                    Toast.makeText(
                        context,
                        getString(R.string.toast_eta_already_added),
                        Toast.LENGTH_SHORT
                    ).show()
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
        title = getString(R.string.title_activity_add_eta)
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        loadRows()
        setSearchResultProvider(this)
    }

    private fun loadRows() {
        lifecycleScope.launch(Dispatchers.Main) {
            mRowsAdapter.clear()
            viewModel.allList?.forEachIndexed { index, routeStopList ->
                // Init Title
                val route = routeStopList.route
                val headerTitle = if (route.isSpecialRoute()) {
                    "${route.routeId} 特別線 (${route.serviceType}) - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
                } else {
                    "${route.routeId} - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
                }

                if (mQuery.isNotEmpty() && !route.routeId.startsWith(mQuery, true)) return@forEachIndexed

                // Init Row
                val listRowAdapter =
                    ArrayObjectAdapter(AddEtaCardPresenter(requireContext(), clickListener))

                val filteredList = routeStopList.stopList.map {
                    Card.RouteStopCard(
                        route = routeStopList.route,
                        stop = it
                    )
                }

                listRowAdapter.addAll(0, filteredList)
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
        if (newQuery.isEmpty()) {
            loadQuery("")
        }
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
                mQuery = it
                loadRows()
            }
        }
    }
}