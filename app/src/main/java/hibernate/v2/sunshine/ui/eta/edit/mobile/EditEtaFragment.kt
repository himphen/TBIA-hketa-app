package hibernate.v2.sunshine.ui.eta.edit.mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentEditEtaBinding
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.transport.MTRTransportRoute
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.base.ItemTouchHelperCallback
import hibernate.v2.sunshine.ui.eta.edit.EditEtaViewModel
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.swap
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import kotlin.coroutines.resume

class EditEtaFragment : BaseFragment<FragmentEditEtaBinding>() {

    private lateinit var menu: Menu
    private lateinit var touchHelper: ItemTouchHelper

    companion object {
        fun getInstance() = EditEtaFragment()
    }

    private val adapter: EditEtaAdapter by lazy {
        EditEtaAdapter(
            onItemClick = { card: Card.SettingsEtaItemCard ->
                if (!isEditingOrdering) {
                    showRemoveEtaConfirmDialog(card)
                }
            },
            onItemMoveCallback = { fromPosition: Int, toPosition: Int ->
                if (!isEditingOrdering) {
                    toggleEditingState(true)
                }

                savedEtaCardList.swap(fromPosition, toPosition)
                adapter.move(fromPosition, toPosition)
            },
            requestDrag = { viewHolder: EditEtaViewHolder ->
                touchHelper.startDrag(viewHolder)
            }
        )
    }

    var isEditingOrdering = false

    private fun showRemoveEtaConfirmDialog(card: Card.SettingsEtaItemCard) {
        context?.let { context ->
            MaterialDialog(context)
                .title(R.string.dialog_settings_eta_remove_title)
                .apply {
                    if (card.route is MTRTransportRoute) {
                        message(
                            text = getString(
                                R.string.dialog_settings_eta_remove_mtr_message,
                                card.route.routeInfo.nameTc,
                                card.stop.nameTc
                            )
                        )
                    } else {
                        message(
                            text = getString(
                                R.string.dialog_settings_eta_remove_message,
                                card.route.routeNo,
                                card.stop.nameTc
                            )
                        )
                    }
                }
                .positiveButton(R.string.dialog_settings_eta_remove_pos_btn) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.clearData(card.entity)
                        val position = savedEtaCardList.indexOf(card)

                        val currentEtaOrderList = viewModel.getEtaOrderList()
                        val updatedEtaOrderList = currentEtaOrderList.filterNot {
                            it.id == card.entity.id
                        }.mapIndexed { index, etaOrderEntity ->
                            EtaOrderEntity(id = etaOrderEntity.id, position = index)
                        }
                        viewModel.updateEtaOrderList(updatedEtaOrderList)

                        savedEtaCardList.remove(card)
                        adapter.remove(position)

                        activity?.setResult(Activity.RESULT_OK)
                    }
                }
                .negativeButton(R.string.dialog_cancel_btn)
                .show()
        }
    }

    private val viewModel by inject<EditEtaViewModel>()
    private var savedEtaCardList: MutableList<Card.SettingsEtaItemCard> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RouteAndStopListDataHolder.cleanData()
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initUi()
        initData()
    }

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            savedEtaCardList.clear()
            savedEtaCardList = it.map { card ->
                card as Card.SettingsEtaItemCard
            }.toMutableList()
            updateRows()
        }
    }

    private fun initUi() {
        val viewBinding = viewBinding!!
        viewBinding.recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerView.addItemDecoration(dividerItemDecoration)
        touchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        touchHelper.attachToRecyclerView(viewBinding.recyclerView)
    }

    private fun initData() {
        viewModel.getSavedEtaCardList()
    }

    private fun updateRows() {
        if (savedEtaCardList.isEmpty()) {
            viewBinding?.apply {
                emptyViewCl.root.visible()
                emptyViewCl.emptyDescTv.text = getString(R.string.empty_eta_list)
                recyclerView.gone()
            }
        } else {
            viewBinding?.apply {
                emptyViewCl.root.gone()
                recyclerView.visible()
            }
        }
        adapter.setData(savedEtaCardList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.settings_eta, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_eta_action_save_order -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    saveNewEtaOrder()
                    toggleEditingState(false)
                }
                return true
            }
            android.R.id.home -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    checkSaveNewEtaOrder()
                    activity?.finish()
                }
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    suspend fun checkSaveNewEtaOrder() {
        if (!isEditingOrdering) return
        val context = context ?: return

        val result = suspendCancellableCoroutine<Boolean> { cont ->
            MaterialDialog(context)
                .message(R.string.dialog_check_edit_eta_order_description)
                .positiveButton(R.string.dialog_check_edit_eta_order_save_btn) {
                    cont.resume(true)
                }
                .negativeButton(R.string.dialog_check_edit_eta_order_leave_btn) {
                    cont.resume(false)
                }
                .onCancel {
                    cont.resume(false)
                }
                .show()
        }

        if (result) {
            saveNewEtaOrder()
        }

        toggleEditingState(false)
    }

    private suspend fun saveNewEtaOrder() {
        val updatedEtaOrderList =
            savedEtaCardList.mapIndexed { index, card ->
                EtaOrderEntity(id = card.entity.id, position = index)
            }
        viewModel.updateEtaOrderList(updatedEtaOrderList)
        activity?.setResult(Activity.RESULT_OK)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun toggleEditingState(startEditing: Boolean) {
        if (startEditing) {
            isEditingOrdering = true
            menu.findItem(R.id.edit_eta_action_save_order)?.isVisible = true
        } else {
            isEditingOrdering = false
            menu.findItem(R.id.edit_eta_action_save_order)?.isVisible = false
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentEditEtaBinding.inflate(inflater, container, false)
}