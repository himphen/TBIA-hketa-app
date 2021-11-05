package hibernate.v2.sunshine.ui.eta.add.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaRouteBinding
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.add.AddEtaMobileViewModel
import hibernate.v2.sunshine.util.getEnum
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.putEnum
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaRouteFragment : BaseFragment<FragmentAddEtaRouteBinding>() {

    companion object {
        private const val ARG_ETA_TYPE = "ARG_ETA_TYPE"
        fun getInstance(etaType: EtaType) =
            AddEtaRouteFragment().apply {
                arguments = Bundle().apply {
                    putEnum(ARG_ETA_TYPE, etaType)
                }
            }
    }

    private val etaType by lazy {
        arguments?.getEnum(ARG_ETA_TYPE, EtaType.KMB) ?: EtaType.KMB
    }
    private val viewModel: AddEtaMobileViewModel by sharedViewModel()
    private val adapter: AddEtaRouteAdapter by lazy {
        AddEtaRouteAdapter(etaType) { route ->
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
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentAddEtaRouteBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initUi()
        initData()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = adapter
        viewBinding.emptyViewCl.emptyDescTv.setText(R.string.empty_route_list)

        val dividerItemDecoration =
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun initData() {
        viewModel.getTransportRouteList(
            etaType
        )
    }

    private fun initEvent() {
        viewModel.filteredTransportRouteList.onEach {
            if (it.first == etaType) {
                adapter.setRouteData(it.second)

                if (it.second.isEmpty()) {
                    viewBinding?.emptyViewCl?.root?.visible()
                    viewBinding?.recyclerView?.gone()
                } else {
                    viewBinding?.emptyViewCl?.root?.gone()
                    viewBinding?.recyclerView?.visible()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}