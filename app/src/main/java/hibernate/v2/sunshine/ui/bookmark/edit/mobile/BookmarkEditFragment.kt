package hibernate.v2.sunshine.ui.bookmark.edit.mobile

import android.annotation.SuppressLint
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.model.Card
import hibernate.v2.model.dataholder.RouteAndStopListDataHolder
import hibernate.v2.model.transport.route.MtrTransportRoute
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentBookmarkEditBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.base.ItemTouchHelperCallback
import hibernate.v2.sunshine.ui.bookmark.edit.BookmarkEditViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainActivity
import hibernate.v2.sunshine.util.GeneralUtils
import hibernate.v2.sunshine.util.gone
import hibernate.v2.sunshine.util.swap
import hibernate.v2.sunshine.util.toggleSlideUp
import hibernate.v2.sunshine.util.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import kotlin.coroutines.resume

class BookmarkEditFragment : BaseFragment<FragmentBookmarkEditBinding>() {

    private lateinit var menu: Menu
    private lateinit var touchHelper: ItemTouchHelper

    companion object {
        fun getInstance() = BookmarkEditFragment()
    }

    private val adapter: BookmarkEditAdapter by lazy {
        BookmarkEditAdapter(
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
            requestDrag = { viewHolder: BookmarkEditViewHolder ->
                touchHelper.startDrag(viewHolder)
            }
        )
    }

    var isEditingOrdering = false
    var invalidCardList = mutableListOf<Card.SettingsEtaItemCard>()

    private fun showRemoveEtaConfirmDialog(card: Card.SettingsEtaItemCard) {
        context?.let { context ->
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_settings_eta_remove_title)
                .apply {
                    if (card.route is MtrTransportRoute) {
                        val route = card.route as MtrTransportRoute
                        setMessage(
                            getString(
                                R.string.dialog_settings_eta_remove_mtr_message,
                                route.routeInfo.getLocalisedName(
                                    GeneralUtils.getTransportationLanguage(
                                        context
                                    )
                                ),
                                card.stop.getLocalisedName(context)
                            )
                        )
                    } else {
                        setMessage(
                            getString(
                                R.string.dialog_settings_eta_remove_message,
                                card.route.routeNo,
                                card.stop.getLocalisedName(context)
                            )
                        )
                    }
                }
                .setPositiveButton(R.string.dialog_settings_eta_remove_pos_btn) { dialog, _ ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.removeEta(card.entity)
                        val position = savedEtaCardList.indexOf(card)

                        val currentEtaOrderList = viewModel.getEtaOrderList()
                        val updatedEtaOrderList = currentEtaOrderList.filterNot {
                            it.id == card.entity.id
                        }.mapIndexed { index, SavedEtaOrderEntity ->
                            SavedEtaOrderEntity(id = SavedEtaOrderEntity.id, position = index)
                        }
                        viewModel.updateEtaOrderList(updatedEtaOrderList)

                        savedEtaCardList.remove(card)
                        adapter.remove(position)

                        activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
                        dialog.dismiss()
                    }
                }
                .setNegativeButton(R.string.dialog_cancel_btn) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private val viewModel by inject<BookmarkEditViewModel>()
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

            val cardList = it.mapNotNull { card1 ->
                val card = card1 as Card.SettingsEtaItemCard
                if (!card.isValid) {
                    invalidCardList.add(card)
                    return@mapNotNull null
                }
                return@mapNotNull card
            }.toMutableList()

            savedEtaCardList.addAll(cardList)
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
        if (invalidCardList.isNotEmpty()) {
            viewBinding?.apply {
                etaInvalidHintsTv.toggleSlideUp(true)
            }

            lifecycleScope.launch {
                invalidCardList.forEach {
                    viewModel.removeEta(it.entity)
                }
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
            MaterialAlertDialogBuilder(context)
                .setMessage(R.string.dialog_check_edit_eta_order_description)
                .setPositiveButton(R.string.dialog_check_edit_eta_order_save_btn) { _, _ ->
                    cont.resume(true)
                }
                .setNegativeButton(R.string.dialog_check_edit_eta_order_leave_btn) { _, _ ->
                    cont.resume(false)
                }
                .setOnCancelListener {
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
                SavedEtaOrderEntity(id = card.entity.id, position = index)
            }
        viewModel.updateEtaOrderList(updatedEtaOrderList)
        activity?.setResult(MainActivity.ACTIVITY_RESULT_SAVED_BOOKMARK)
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
    ) = FragmentBookmarkEditBinding.inflate(inflater, container, false)
}
