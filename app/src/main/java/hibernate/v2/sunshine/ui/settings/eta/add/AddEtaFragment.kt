package hibernate.v2.sunshine.ui.settings.eta.add

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
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
        override fun onItemClick(card: Card.RouteStopAddCard) {
            lifecycleScope.launch {
                val isExisting = viewModel.hasEtaInDb(
                    stopId = card.stop.stopId,
                    routeId = card.route.routeId,
                    bound = card.route.bound,
                    serviceType = card.route.serviceType,
                    seq = card.stop.seq!!,
                    brand = card.route.brand
                )

                if (isExisting) {
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
                    seq = card.stop.seq!!,
                    brand = card.route.brand
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
        super.onCreate(savedInstanceState ?: Bundle())
        title = getString(R.string.title_activity_add_eta)
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        setSearchResultProvider(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()

        loadRows()
    }

    private fun initEvent() {

    }

    private fun loadRows() {
        lifecycleScope.launch(Dispatchers.Main) {
            mRowsAdapter.clear()
            viewModel.allTransportRouteList?.forEachIndexed { index, listForRowAdapter ->
                if (mQuery.isNotEmpty()
                    && !listForRowAdapter.route.routeId.startsWith(mQuery, true)
                ) return@forEachIndexed

                // Init Row
                val listRowAdapter =
                    ArrayObjectAdapter(AddEtaCardPresenter(requireContext(), clickListener))

                listRowAdapter.addAll(0, listForRowAdapter.filteredList)
                val header = HeaderItem(index.toLong(), listForRowAdapter.headerTitle)
                val listRow = ListRow(header, listRowAdapter)
                mRowsAdapter.add(listRow)
            }
        }
    }

    override fun getResultsAdapter() = mRowsAdapter

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