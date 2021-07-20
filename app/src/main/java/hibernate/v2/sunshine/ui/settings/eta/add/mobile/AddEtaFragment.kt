package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentAddEtaBinding
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.base.BaseActivity
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import hibernate.v2.sunshine.ui.view.ColorBarDrawable
import hibernate.v2.sunshine.util.afterTextChanged
import hibernate.v2.sunshine.util.onTextChanged
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEtaFragment : BaseFragment<FragmentAddEtaBinding>() {

    companion object {
        const val searchDelay = 500L
        const val kDrawableRight = 2
        fun getInstance() = AddEtaFragment()
    }

    private val viewModel: AddEtaViewModel by sharedViewModel()
    private val currentSelectionType by lazy {
        when {
            viewModel.selectedRoute.value != null -> SelectionType.Stop
            viewModel.selectedEtaType.value != null -> SelectionType.Route
            else -> SelectionType.EtaType
        }
    }
    private val adapter: AddEtaAdapter by lazy {
        AddEtaAdapter(currentSelectionType, object : AddEtaAdapter.ItemListener {
            override fun onEtaTypeSelected(etaType: AddEtaViewModel.EtaType) {
                viewModel.selectedEtaType.value = etaType

                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    setCustomAnimations(
                        R.anim.fragment_fade_enter,
                        R.anim.fragment_fade_exit
                    )
                    add(R.id.container, getInstance())
                    addToBackStack(null)
                    commit()
                }
            }

            override fun onRouteSelected(route: RouteForRowAdapter) {
                viewModel.selectedRoute.value = route

                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    setCustomAnimations(
                        R.anim.fragment_fade_enter,
                        R.anim.fragment_fade_exit
                    )
                    add(R.id.container, getInstance())
                    addToBackStack(null)
                    commit()
                }
            }

            override fun onStopSelected(card: Card.RouteStopAddCard) {
                viewModel.saveStop(card)
            }
        })
    }

    private val drawableRight: Drawable?
        get() {
            val viewBinding = viewBinding ?: return null

            return if (viewBinding.searchEt.compoundDrawables.asList().size > kDrawableRight) {
                viewBinding.searchEt.compoundDrawables[kDrawableRight]
            } else {
                null
            }
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

        when (currentSelectionType) {
            SelectionType.EtaType -> {
                val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
                viewBinding.hintCl.visibility = View.GONE
                viewBinding.searchCl.visibility = View.GONE
            }
            SelectionType.Route -> {
                val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
                viewBinding.hintCl.visibility = View.VISIBLE
                viewBinding.searchCl.visibility = View.VISIBLE

                viewBinding.searchEt.apply {
                    isLongClickable = false
                    setOnTouchListener(
                        View.OnTouchListener { _, event ->
                            drawableRight?.let {
                                if (event.action == MotionEvent.ACTION_UP) {
                                    if (event.rawX >= right - it.bounds.width()
                                    ) {
                                        text.clear()
                                        performClick()
                                        return@OnTouchListener true
                                    }
                                }
                            }
                            false
                        }
                    )

                    setOnEditorActionListener { v, actionId, _ ->
                        val text = v.text.toString()
                        if (actionId == EditorInfo.IME_ACTION_SEARCH && text.isNotBlank()) {
                            (activity as? BaseActivity<out ViewBinding>)?.hideSoftKeyboard()
                            return@setOnEditorActionListener true
                        }

                        return@setOnEditorActionListener false
                    }

                    onTextChanged()
                        .onEach {
                            updateCloseButton()
                        }
                        .launchIn(lifecycleScope)

                    afterTextChanged()
                        .debounce(searchDelay)
                        .onEach {
                            viewModel.searchRouteKeyword.value = it
                            viewModel.searchRoute()
                        }
                        .launchIn(lifecycleScope)

                    doOnLayout {
                        // initial hide close button
                        updateCloseButton()
                    }
                }

                val etaTypeName = viewModel.selectedEtaType.value!!.etaTypeName(requireContext())
                val etaTypeColors =
                    viewModel.selectedEtaType.value!!.etaTypeColors(requireContext())

                viewBinding.hintTv.text = getString(R.string.text_add_eta_hint_route, etaTypeName)
                viewBinding.hintCl.background = ColorBarDrawable(etaTypeColors)
            }
            SelectionType.Stop -> {
                val selectedRoute = viewModel.selectedRoute.value!!.route
                viewBinding.hintCl.visibility = View.VISIBLE
                viewBinding.searchCl.visibility = View.GONE
                viewBinding.hintTv.text = getString(
                    R.string.text_add_eta_hint_stop,
                    selectedRoute.getDirectionWithRouteText(requireContext())
                )
                val etaTypeColors =
                    viewModel.selectedEtaType.value!!.etaTypeColors(requireContext())
                viewBinding.hintCl.background = ColorBarDrawable(etaTypeColors)
            }
        }
    }

    private fun initData() {
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
            SelectionType.Route -> viewModel.getTransportRouteList(
                requireContext(),
                viewModel.selectedEtaType.value
            )
            SelectionType.Stop -> {
                viewModel.selectedRoute.value?.filteredList?.let { stopList ->
                    adapter.setStopData(stopList)
                }
            }
        }
    }

    private fun initEvent() {
        viewModel.filteredTransportRouteList.onEach { routeList ->
            adapter.setRouteData(routeList)
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

    private fun updateCloseButton() {
        val viewBinding = viewBinding ?: return
        if (viewBinding.searchEt.text.isEmpty()) {
            drawableRight?.alpha = 0
        } else {
            drawableRight?.alpha = 100
        }
    }

    enum class SelectionType {
        EtaType, Route, Stop
    }

    override fun onDestroyView() {
        when (currentSelectionType) {
            SelectionType.Route -> viewModel.selectedEtaType.value == null
            SelectionType.Stop -> viewModel.selectedRoute.value == null
            SelectionType.EtaType -> {
            }
        }
        super.onDestroyView()
    }
}