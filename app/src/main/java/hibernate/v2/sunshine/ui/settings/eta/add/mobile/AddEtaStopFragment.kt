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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaStopFragment : BaseFragment<FragmentAddEtaBinding>() {

    companion object {
        fun getInstance() = AddEtaStopFragment()
    }

    private val viewModel: AddEtaViewModel by sharedViewModel()
    private val adapter: AddEtaStopAdapter by lazy {
        AddEtaStopAdapter(object : AddEtaStopAdapter.ItemListener {
            override fun onStopSelected(card: Card.RouteStopAddCard) {
                viewModel.saveStop(card)
            }
        })
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        val selectedRoute = viewModel.selectedRoute.value!!.route
        viewBinding.hintTv.text = getString(
            R.string.text_add_eta_hint_stop,
            selectedRoute.getDirectionWithRouteText(requireContext())
        )
        viewBinding.hintCl.setBackgroundColor(selectedRoute.getColor(requireContext()))
    }

    private fun initData() {
        viewModel.selectedRoute.value?.filteredList?.let { stopList ->
            adapter.setStopData(stopList)
        }
    }

    private fun initEvent() {
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

    override fun onDestroyView() {
        viewModel.selectedRoute.value = null
        super.onDestroyView()
    }
}