package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import hibernate.v2.sunshine.util.getEnum
import hibernate.v2.sunshine.util.putEnum
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaRouteFragment : BaseFragment<FragmentAddEtaBinding>() {

    companion object {
        private const val ARG_ETA_TYPE = "ARG_ETA_TYPE"
        const val kDrawableRight = 2
        fun getInstance(etaType: AddEtaViewModel.EtaType) =
            AddEtaRouteFragment().apply {
                arguments = Bundle().apply {
                    putEnum(ARG_ETA_TYPE, etaType)
                }
            }
    }

    private val etaType by lazy {
        arguments?.getEnum(ARG_ETA_TYPE, AddEtaViewModel.EtaType.KMB) ?: AddEtaViewModel.EtaType.KMB
    }
    private val viewModel: AddEtaViewModel by sharedViewModel()
    private val adapter: AddEtaAdapter by lazy {
        AddEtaAdapter(AddEtaAdapter.SelectionType.Route, object : AddEtaAdapter.ItemListener {
            override fun onRouteSelected(route: RouteForRowAdapter) {
                viewModel.selectedEtaType.value = etaType
                viewModel.selectedRoute.value = route

                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    add(R.id.container, AddEtaStopFragment.getInstance())
                    addToBackStack(null)
                    commit()
                }
            }

            override fun onStopSelected(card: Card.RouteStopAddCard) {
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
        initUi()
        initData()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = adapter

        val dividerItemDecoration =
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
        viewBinding.hintCl.visibility = View.GONE
    }

    private fun initData() {
        viewModel.getTransportRouteList(
            requireContext(),
            etaType
        )
    }

    private fun initEvent() {
        viewModel.filteredTransportRouteList.onEach {
            if (it.first == etaType) {
                adapter.setRouteData(it.second)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}