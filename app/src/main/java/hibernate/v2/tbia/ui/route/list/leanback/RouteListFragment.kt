package hibernate.v2.tbia.ui.route.list.leanback

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import hibernate.v2.MR
import hibernate.v2.model.Card
import hibernate.v2.model.dataholder.AddEtaRowItem
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.ui.bookmark.BookmarkSaveViewModel
import hibernate.v2.tbia.ui.route.list.leanback.RouteListActivity.Companion.ARG_ETA_TYPE
import hibernate.v2.tbia.util.getEnum
import hibernate.v2.utils.localized
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class RouteListFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private lateinit var mRowsAdapter: ArrayObjectAdapter
    private val viewModel by stateViewModel<RouteListLeanbackViewModel>()
    private val bookmarkSaveViewModel by stateViewModel<BookmarkSaveViewModel>()

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

        bookmarkSaveViewModel.isAddEtaSuccessful.onEach {
            if (it) {
                activity?.setResult(Activity.RESULT_OK)
            }
            Snackbar.make(
                requireView(),
                MR.strings.toast_eta_added.localized(requireContext()),
                Snackbar.LENGTH_LONG
            ).show()
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun loadRows(routeList: List<AddEtaRowItem>) {
        mRowsAdapter.clear()
        routeList.forEachIndexed { index, listForRowAdapter ->
            // Init Row
            val listRowAdapter =
                ArrayObjectAdapter(
                    RouteListCardPresenter(requireContext()) {
                        bookmarkSaveViewModel.saveStop(it)
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
            context?.let { context ->
                viewModel.searchRoute(context, etaType)
            }
        }

        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }
}
