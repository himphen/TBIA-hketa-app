package hibernate.v2.sunshine.ui.settings.eta.listing.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.FragmentSettingsEtaListingBinding
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.base.ItemTouchHelperCallback
import hibernate.v2.sunshine.ui.settings.eta.SettingsEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.mobile.AddEtaActivity
import hibernate.v2.sunshine.util.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsEtaListingFragment : BaseFragment<FragmentSettingsEtaListingBinding>() {

    private var addEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getSavedEtaCardList()

            activity?.setResult(Activity.RESULT_OK)
        }
    }

    companion object {
        fun getInstance() = SettingsEtaListingFragment()
    }

    private val adapter: SettingsEtaListingAdapter by lazy {
        SettingsEtaListingAdapter(object : SettingsEtaListingAdapter.ItemListener {
            override fun onItemClick(card: Card.SettingsEtaItemCard) {
                showRemoveEtaConfirmDialog(card)
            }

            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                lifecycleScope.launch(Dispatchers.Main) {
                    val currentEtaOrderList = viewModel.getEtaOrderList().toMutableList()
                    currentEtaOrderList.swap(fromPosition, toPosition)
                    val updatedEtaOrderList =
                        currentEtaOrderList.mapIndexed { index, etaOrderEntity ->
                            EtaOrderEntity(id = etaOrderEntity.id, position = index)
                        }
                    viewModel.updateEtaOrderList(updatedEtaOrderList)

                    savedEtaCardList.swap(fromPosition, toPosition)
                    adapter.move(fromPosition, toPosition)
                }
            }
        })
    }

    private fun showRemoveEtaConfirmDialog(card: Card.SettingsEtaItemCard) {
        context?.let { context ->
            MaterialDialog(context)
                .title(R.string.dialog_settings_eta_remove_title)
                .message(
                    text = getString(
                        R.string.dialog_settings_eta_remove_message,
                        card.route.routeNo,
                        card.stop.nameTc
                    )
                )
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

    private val viewModel by inject<SettingsEtaViewModel>()
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
        val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        touchHelper.attachToRecyclerView(viewBinding.recyclerView)
    }

    private fun initData() {
        viewModel.getSavedEtaCardList()
    }

    private fun updateRows() {
        adapter.setData(savedEtaCardList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_eta, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_eta_action_add -> {
                addEtaLauncher.launch(Intent(context, AddEtaActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsEtaListingBinding.inflate(inflater, container, false)
}