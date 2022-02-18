package hibernate.v2.sunshine.ui.eta.add.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaRouteBinding
import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.eta.add.AddEtaMobileViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.ui.route.mobile.RouteDetailsActivity
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

    private var etaAddedLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK) {
                activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
            }
        }

    private val etaType by lazy {
        arguments?.getEnum(ARG_ETA_TYPE, EtaType.KMB) ?: EtaType.KMB
    }
    private val viewModel: AddEtaMobileViewModel by sharedViewModel()
    private val adapter: AddEtaRouteAdapter by lazy {
        AddEtaRouteAdapter(etaType) { route ->
            viewModel.selectedEtaType.value = etaType

            etaAddedLauncher.launch(RouteDetailsActivity.getLaunchIntent(context, route, etaType))
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
            viewBinding?.loadingProgressIndicator?.gone(true)
            if (it.first == etaType) {
                val list = it.second.sorted()
                adapter.setRouteData(list)

                if (list.isEmpty()) {
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
