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
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.RouteStopList
import hibernate.v2.sunshine.repository.RouteStopListDataHolder
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AddEtaFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private lateinit var list: ArrayList<RouteStopList>
    private val viewModel by inject<AddEtaViewModel>()

    companion object {
        fun getInstance() = AddEtaFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Search"

        list = RouteStopListDataHolder.data ?: arrayListOf()

        loadRows()
        setSearchResultProvider(this)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val listener = object : AddEtaCardPresenter.ClickListener {
            override fun onItemClick(card: Card.RouteStopCard) {
                lifecycleScope.launch {
                    val data = viewModel.getData(
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

                    viewModel.addData(
                        EtaEntity(
                            stopId = card.stop.stopId,
                            routeId = card.route.routeId,
                            bound = card.route.bound,
                            serviceType = card.route.serviceType,
                            seq = card.stop.seq!!
                        )
                    )

                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                }
            }
        }
        list.forEachIndexed { index, routeStopList ->
            val listRowAdapter = ArrayObjectAdapter(AddEtaCardPresenter(requireContext(), listener))
            listRowAdapter.addAll(0, routeStopList.stopList.map {
                Card.RouteStopCard(
                    route = routeStopList.route,
                    stop = it
                )
            })

            val route = routeStopList.route

            val headerTitle = if (route.isSpecialRoute()) {
                "${route.routeId} 特別線 (${route.serviceType}) - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
            } else {
                "${route.routeId} - ${routeStopList.route.origTc} 往 ${routeStopList.route.destTc}"
            }

            val header = HeaderItem(index.toLong(), headerTitle)
            val imageListRow = ListRow(header, listRowAdapter)
            rowsAdapter.add(imageListRow)
        }

        //set new adapter to BrowseSupportFragment
        mRowsAdapter = rowsAdapter
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return mRowsAdapter
    }

    override fun onQueryTextChange(newQuery: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }
}