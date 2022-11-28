package hibernate.v2.tbia.ui.route.list.mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.himphen.logger.Logger
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.FragmentRouteListBinding
import hibernate.v2.tbia.ui.base.BaseFragment
import hibernate.v2.tbia.ui.main.mobile.MainActivity
import hibernate.v2.tbia.ui.route.details.mobile.RouteDetailsActivity
import hibernate.v2.tbia.util.getEnum
import hibernate.v2.tbia.util.gone
import hibernate.v2.tbia.util.putEnum
import hibernate.v2.tbia.util.visible
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RouteListRouteFragment : BaseFragment<FragmentRouteListBinding>() {

    companion object {
        private const val ARG_ETA_TYPE = "ARG_ETA_TYPE"
        fun getInstance(etaType: EtaType) =
            RouteListRouteFragment().apply {
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
    private val viewModel: RouteListMobileViewModel by sharedViewModel()
    private val adapter: RouteListRouteItemAdapter by lazy {
        RouteListRouteItemAdapter(etaType) { route ->
            etaAddedLauncher.launch(RouteDetailsActivity.getLaunchIntent(context, route, etaType))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentRouteListBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initUi()
        initData()
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        viewBinding.emptyViewCl.root.gone()
        viewBinding.recyclerView.gone()

        viewBinding.recyclerView.adapter = adapter
        viewBinding.emptyViewCl.emptyDescTv.setText(R.string.empty_route_list)

        val dividerItemDecoration =
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)

        viewBinding.recyclerView.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int) =
                EdgeEffect(view.context).apply { color = etaType.color().colorInt() }
        }
    }

    private fun initData() {
    }

    private fun initEvent() {
        viewModel.filteredTransportRouteList
            .onEach {
                Logger.d("ac1 filteredTransportRouteList: ${it.first} - ${it.second.isEmpty()} - $etaType")
                if (it.first == etaType) {
                    val list = it.second
                    if (list.isEmpty()) {
                        viewBinding?.emptyViewCl?.root?.visible()
                        viewBinding?.recyclerView?.gone()
                    } else {
                        viewBinding?.emptyViewCl?.root?.gone()
                        viewBinding?.recyclerView?.visible()
                    }
                    adapter.submitList(list)
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Logger.d("ac1 repeatOnLifecycle: $etaType")
                viewModel.getTransportRouteList(requireContext(), etaType)
            }
        }
    }
}
