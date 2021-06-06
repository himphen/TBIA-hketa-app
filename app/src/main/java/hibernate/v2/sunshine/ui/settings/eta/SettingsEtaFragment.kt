package hibernate.v2.sunshine.ui.settings.eta

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import hibernate.v2.sunshine.model.Card

class SettingsEtaFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 1
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

        fun getInstance() = SettingsEtaFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Meow"
        setupRowAdapter()
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = COLUMNS
        setGridPresenter(gridPresenter)
        val cardPresenter = SettingsEtaCardPresenter(requireContext())
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
        prepareEntranceTransition()
        Handler(Looper.getMainLooper()).postDelayed({
            createRows()
            startEntranceTransition()
        }, 1000)
    }

    private fun createRows() {
        val list = arrayListOf<Card.SettingsCard>()
        list.add(
            Card.SettingsCard(
                title = "title",
                type = Card.SettingsCard.Type.ETA,
                localImageResourceName = "ic_settings_settings"
            )
        )
        list.add(
            Card.SettingsCard(
                title = "title",
                type = Card.SettingsCard.Type.NONE,
                localImageResourceName = "ic_settings_settings"
            )
        )
        list.add(
            Card.SettingsCard(
                title = "title",
                type = Card.SettingsCard.Type.NONE,
                localImageResourceName = "ic_settings_settings"
            )
        )
        list.add(
            Card.SettingsCard(
                title = "title",
                type = Card.SettingsCard.Type.NONE,
                localImageResourceName = "ic_settings_settings"
            )
        )

        mAdapter!!.addAll(0, list)
    }
}