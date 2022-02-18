package hibernate.v2.sunshine.ui.route.list.leanback

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
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.AddEtaRowItem
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.route.list.RouteListViewModel
import hibernate.v2.sunshine.ui.route.list.leanback.RouteListActivity.Companion.ARG_ETA_TYPE
import hibernate.v2.sunshine.util.getEnum
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class RouteListFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val viewModel by stateViewModel<RouteListViewModel>()

    private val etaType: EtaType by lazy {
        arguments?.getEnum(ARG_ETA_TYPE, EtaType.KMB) ?: EtaType.KMB
    }

    private var searchJob: Job? = null

    companion object {
        const val searchDelay = 500L
        fun getInstance(bundle: Bundle?) = RouteListFragment().apply { arguments = bundle }
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

        viewModel.getTransportRouteList(requireContext(), etaType)
    }

    private fun initEvent() {
        viewModel.filteredTransportRouteList.onEach {
            loadRows(it.second)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isAddEtaSuccessful.onEach {
            if (it) {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.toast_eta_already_added),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun loadRows(routeList: List<AddEtaRowItem>) {
        mRowsAdapter.clear()
        routeList.forEachIndexed { index, listForRowAdapter ->
            // Init Row
            val listRowAdapter =
                ArrayObjectAdapter(
                    RouteListCardPresenter(requireContext()) {
                        viewModel.saveStop(it)
                    }
                )

            listRowAdapter.addAll(
                0,
                listForRowAdapter.filteredList.map {
                    Card.RouteStopAddCard(
                        route = listForRowAdapter.route,
                        stop = it
                    )
                }
            )
            val header = HeaderItem(index.toLong(), listForRowAdapter.headerTitle)
            val listRow = ListRow(header, listRowAdapter)
            mRowsAdapter.add(listRow)
        }
    }

    override fun getResultsAdapter() = mRowsAdapter

    override fun onQueryTextChange(newQuery: String): Boolean {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(searchDelay)
            viewModel.searchRouteKeyword.value = newQuery
            viewModel.searchRoute(etaType)
        }

        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }
}
