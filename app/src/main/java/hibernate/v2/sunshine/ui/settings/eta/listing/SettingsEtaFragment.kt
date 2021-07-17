package hibernate.v2.sunshine.ui.settings.eta.listing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.model.EditEta
import hibernate.v2.sunshine.repository.RouteAndStopListDataHolder
import hibernate.v2.sunshine.ui.settings.eta.SettingsEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaActivity
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogActivity
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogActivity.Companion.ARG_RESULT_CODE
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogFragment.Companion.ACTION_ID_MOVE_DOWN
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogFragment.Companion.ACTION_ID_MOVE_UP
import hibernate.v2.sunshine.ui.settings.eta.edit.EditEtaDialogFragment.Companion.ACTION_ID_REMOVE
import hibernate.v2.sunshine.util.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsEtaFragment : VerticalGridSupportFragment() {

    var addEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getSavedEtaCardList()
        }
    }

    var editEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch(Dispatchers.Main) {
                when (result.data?.getLongExtra(ARG_RESULT_CODE, -1)) {
                    ACTION_ID_MOVE_UP -> {
                        viewModel.editCard.value?.let { card ->
                            val position = savedEtaCardList.indexOf(card)
                            val newPosition = position - 1

                            val currentEtaOrderList = viewModel.getEtaOrderList().toMutableList()
                            currentEtaOrderList.swap(position - OFFSET, newPosition - OFFSET)
                            val updatedEtaOrderList =
                                currentEtaOrderList.mapIndexed { index, etaOrderEntity ->
                                    EtaOrderEntity(id = etaOrderEntity.id, position = index)
                                }
                            viewModel.updateEtaOrderList(updatedEtaOrderList)

                            mAdapter?.move(position, newPosition)
                            savedEtaCardList.swap(position, newPosition)
                        }
                    }
                    ACTION_ID_MOVE_DOWN -> {
                        viewModel.editCard.value?.let { card ->
                            val position = savedEtaCardList.indexOf(card)
                            val newPosition = position + 1

                            val currentEtaOrderList = viewModel.getEtaOrderList().toMutableList()
                            currentEtaOrderList.swap(position - OFFSET, newPosition - OFFSET)
                            val updatedEtaOrderList =
                                currentEtaOrderList.mapIndexed { index, etaOrderEntity ->
                                    EtaOrderEntity(id = etaOrderEntity.id, position = index)
                                }
                            viewModel.updateEtaOrderList(updatedEtaOrderList)

                            mAdapter?.move(position, newPosition)
                            savedEtaCardList.swap(position, newPosition)
                        }
                    }
                    ACTION_ID_REMOVE -> {
                        viewModel.editCard.value?.let { card ->
                            card as Card.SettingsEtaItemCard
                            viewModel.clearData(card.entity)

                            val currentEtaOrderList = viewModel.getEtaOrderList()
                            val updatedEtaOrderList = currentEtaOrderList.filterNot {
                                it.id == card.entity.id
                            }.mapIndexed { index, etaOrderEntity ->
                                EtaOrderEntity(id = etaOrderEntity.id, position = index)
                            }
                            viewModel.updateEtaOrderList(updatedEtaOrderList)

                            mAdapter?.remove(card)
                            savedEtaCardList.remove(card)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE
        private const val OFFSET = 1

        fun getInstance() = SettingsEtaFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<SettingsEtaViewModel>()
    private var savedEtaCardList: MutableList<Card.SettingsEtaCard> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_activity_settings_eta)
        RouteAndStopListDataHolder.cleanData()
        setupRowAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initData()
    }

    private fun initEvent() {
        viewModel.savedEtaCardList.observe(viewLifecycleOwner) {
            savedEtaCardList.clear()
            savedEtaCardList = it
            savedEtaCardList.add(
                0,
                Card.SettingsEtaAddCard()
            )
            updateRows()
        }
    }

    private fun initData() {
        viewModel.getSavedEtaCardList()
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = COLUMNS
        setGridPresenter(gridPresenter)
        val cardPresenter = SettingsEtaCardPresenter(requireContext(),
            object : SettingsEtaCardPresenter.ClickListener {
                override fun onItemClick(card: Card.SettingsEtaCard) {
                    when (card) {
                        is Card.SettingsEtaAddCard -> {
                            addEtaLauncher.launch(Intent(context, AddEtaActivity::class.java))
                        }
                        is Card.SettingsEtaItemCard -> {
                            viewModel.editCard.postValue(card)

                            editEtaLauncher.launch(
                                Intent(
                                    context,
                                    EditEtaDialogActivity::class.java
                                ).apply {
                                    putExtra(
                                        EditEtaDialogActivity.ARG_BUNDLE,
                                        Bundle().apply {
                                            putParcelable(
                                                EditEtaDialogActivity.ARG_SELECTED_ETA,
                                                EditEta(
                                                    entity = card.entity,
                                                    route = card.route,
                                                    stop = card.stop
                                                )
                                            )

                                            val currentPosition = savedEtaCardList.indexOf(card)
                                            if (currentPosition > 1) {
                                                putString(
                                                    EditEtaDialogActivity.ARG_BEFORE_ETA_ID,
                                                    (savedEtaCardList.getOrNull(currentPosition - 1)
                                                            as? Card.SettingsEtaItemCard)?.entity?.id?.toString()
                                                )
                                            }

                                            if (currentPosition < savedEtaCardList.lastIndex) {
                                                putString(
                                                    EditEtaDialogActivity.ARG_AFTER_ETA_ID,
                                                    (savedEtaCardList.getOrNull(currentPosition + 1)
                                                            as? Card.SettingsEtaItemCard)?.entity?.id?.toString()
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        )
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
    }

    private fun updateRows() {
        mAdapter?.clear()
        mAdapter?.addAll(0, savedEtaCardList)
    }
}