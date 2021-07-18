package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaFragment : BaseFragment<FragmentAddEtaBinding>() {

    companion object {
        fun getInstance() = AddEtaFragment()
    }

    private val viewModel: AddEtaViewModel by sharedViewModel()
    private val currentSelectionType by lazy {
        when {
            viewModel.selectedEtaType.value == null -> SelectionType.Route
            viewModel.selectedRouteStopList.value?.isNotEmpty() == true -> SelectionType.Stop
            else -> SelectionType.EtaType
        }
    }
    private val adapter: AddEtaAdapter by lazy {
        AddEtaAdapter(currentSelectionType, object : AddEtaAdapter.ItemListener {
            override fun onEtaTypeSelected(etaType: AddEtaViewModel.EtaType) {
                viewModel.selectedEtaType.value = etaType
                val fragmentTransaction =
                    activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.add(
                    R.id.container,
                    getInstance()
                )
                fragmentTransaction?.commit()
            }

            override fun onRouteSelected(route: RouteForRowAdapter) {
                viewModel.selectedRouteStopList.value = route.filteredList.toMutableList()
                val fragmentTransaction =
                    activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.add(
                    R.id.container,
                    getInstance()
                )
                fragmentTransaction?.commit()
            }

            override fun onStopSelected(card: Card.RouteStopAddCard) {
                viewModel.saveStop(card)
            }
        })
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentAddEtaBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()

        when (currentSelectionType) {
            SelectionType.EtaType -> loadRows()
            SelectionType.Route -> viewModel.getTransportRouteList(
                requireContext(),
                viewModel.selectedEtaType.value
            )
            SelectionType.Stop -> loadRows()
        }
    }

    private fun initEvent() {
        viewModel.allTransportRouteList.observe(viewLifecycleOwner) {
            loadRows()
        }

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

    private fun loadRows() {
        when (currentSelectionType) {
            SelectionType.EtaType -> {
                adapter.setTypeData(
                    mutableListOf(
                        AddEtaViewModel.EtaType.KMB,
                        AddEtaViewModel.EtaType.NWFB_CTB,
                        AddEtaViewModel.EtaType.GMB
                    )
                )
            }
            SelectionType.Route -> {
                viewModel.allTransportRouteList.value?.let { routeList ->
//                    if (mQuery.isNotEmpty()
//                        && !listForRowAdapter.route.routeNo.startsWith(mQuery, true)
//                    ) return@forEachIndexed

                    adapter.setRouteData(routeList)
                }
            }
            SelectionType.Stop -> {
                viewModel.selectedRouteStopList.value?.let { stopList ->
//                    if (mQuery.isNotEmpty()
//                        && !listForRowAdapter.route.routeNo.startsWith(mQuery, true)
//                    ) return@forEachIndexed

                    adapter.setStopData(stopList)
                }
            }
        }
    }

    enum class SelectionType {
        EtaType, Route, Stop
    }

    override fun onDestroyView() {
        super.onDestroyView()

        when (currentSelectionType) {
            SelectionType.Route -> viewModel.selectedEtaType.value
            SelectionType.Stop -> viewModel.selectedRouteStopList.value == null
            SelectionType.EtaType -> {
            }
        }
    }
}