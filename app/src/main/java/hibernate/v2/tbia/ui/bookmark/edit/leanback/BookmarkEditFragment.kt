package hibernate.v2.tbia.ui.bookmark.edit.leanback

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import hibernate.v2.database.eta.SavedEtaOrderEntity
import hibernate.v2.model.Card
import hibernate.v2.model.dataholder.RouteAndStopListDataHolder
import hibernate.v2.tbia.R
import hibernate.v2.tbia.ui.bookmark.edit.BookmarkEditViewModel
import hibernate.v2.tbia.ui.bookmark.edit.leanback.BookmarkEditConfirmDialogActivity.Companion.ARG_RESULT_CODE
import hibernate.v2.tbia.ui.bookmark.edit.leanback.BookmarkEditConfirmDialogFragment.Companion.ACTION_ID_MOVE_DOWN
import hibernate.v2.tbia.ui.bookmark.edit.leanback.BookmarkEditConfirmDialogFragment.Companion.ACTION_ID_MOVE_UP
import hibernate.v2.tbia.ui.bookmark.edit.leanback.BookmarkEditConfirmDialogFragment.Companion.ACTION_ID_REMOVE
import hibernate.v2.tbia.ui.route.list.leanback.RouteListActivity
import hibernate.v2.tbia.util.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BookmarkEditFragment : VerticalGridSupportFragment() {

    var addEtaLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getSavedEtaCardList()
        }
    }

    var bookmarkEditLauncher = registerForActivityResult(StartActivityForResult()) { result ->
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
                                currentEtaOrderList.mapIndexed { index, SavedEtaOrderEntity ->
                                    SavedEtaOrderEntity(
                                        id = SavedEtaOrderEntity.id,
                                        position = index
                                    )
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
                                currentEtaOrderList.mapIndexed { index, SavedEtaOrderEntity ->
                                    SavedEtaOrderEntity(
                                        id = SavedEtaOrderEntity.id,
                                        position = index
                                    )
                                }
                            viewModel.updateEtaOrderList(updatedEtaOrderList)

                            mAdapter?.move(position, newPosition)
                            savedEtaCardList.swap(position, newPosition)
                        }
                    }
                    ACTION_ID_REMOVE -> {
                        viewModel.editCard.value?.let { card ->
                            card as Card.SettingsEtaItemCard
                            viewModel.removeEta(card.entity)

                            val currentEtaOrderList = viewModel.getEtaOrderList()
                            val updatedEtaOrderList = currentEtaOrderList.filterNot {
                                it.id == card.entity.id
                            }.mapIndexed { index, SavedEtaOrderEntity ->
                                SavedEtaOrderEntity(id = SavedEtaOrderEntity.id, position = index)
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

        fun getInstance() = BookmarkEditFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null
    private val viewModel by inject<BookmarkEditViewModel>()
    private var savedEtaCardList: MutableList<Card.SettingsEtaCard> = mutableListOf()

    var invalidCardList = mutableListOf<Card.SettingsEtaItemCard>()

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
            savedEtaCardList.add(Card.SettingsEtaAddCard())

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

    private fun initData() {
        viewModel.getSavedEtaCardList()
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR, false)
        gridPresenter.keepChildForeground = false
        gridPresenter.numberOfColumns = COLUMNS
        gridPresenter.shadowEnabled = false
        setGridPresenter(gridPresenter)
        val cardPresenter = BookmarkEditCardPresenter(
            requireContext()
        ) { card: Card.SettingsEtaCard ->
            when (card) {
                is Card.SettingsEtaAddCard -> {
                    addEtaLauncher.launch(Intent(context, RouteListActivity::class.java))
                }
                is Card.SettingsEtaItemCard -> {
                    viewModel.editCard.postValue(card)

                    bookmarkEditLauncher.launch(
                        Intent(
                            context,
                            BookmarkEditConfirmDialogActivity::class.java
                        ).apply {
                            putExtra(
                                BookmarkEditConfirmDialogActivity.ARG_BUNDLE,
                                Bundle().apply {
                                    // TODO
//                                    putSerializable(
//                                        BookmarkEditConfirmDialogActivity.ARG_SELECTED_ETA,
//                                        BookmarkEdit(
//                                            entity = card.entity,
//                                            route = card.route,
//                                            stop = card.stop
//                                        )
//                                    )
//
//                                    val currentPosition = savedEtaCardList.indexOf(card)
//                                    if (currentPosition > 1) {
//                                        putString(
//                                            BookmarkEditConfirmDialogActivity.ARG_BEFORE_ETA_ID,
//                                            (
//                                                savedEtaCardList.getOrNull(currentPosition - 1)
//                                                    as? Card.SettingsEtaItemCard
//                                                )?.entity?.id?.toString()
//                                        )
//                                    }
//
//                                    if (currentPosition < savedEtaCardList.lastIndex) {
//                                        putString(
//                                            BookmarkEditConfirmDialogActivity.ARG_AFTER_ETA_ID,
//                                            (
//                                                savedEtaCardList.getOrNull(currentPosition + 1)
//                                                    as? Card.SettingsEtaItemCard
//                                                )?.entity?.id?.toString()
//                                        )
//                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
    }

    private fun updateRows() {
        mAdapter?.clear()
        mAdapter?.addAll(0, savedEtaCardList)

        if (invalidCardList.isNotEmpty()) {
            Snackbar.make(requireView(), R.string.text_edit_eta_invalid_hint, Toast.LENGTH_LONG).show()

            lifecycleScope.launch {
                invalidCardList.forEach {
                    viewModel.removeEta(it.entity)
                }
            }
        }
    }
}
