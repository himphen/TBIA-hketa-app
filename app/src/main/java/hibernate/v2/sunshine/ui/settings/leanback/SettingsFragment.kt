package hibernate.v2.sunshine.ui.settings.leanback

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.VerticalGridPresenter
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card
import hibernate.v2.sunshine.ui.eta.edit.leanback.EditEtaActivity
import hibernate.v2.sunshine.ui.settings.eta.layout.leanback.EtaLayoutSelectionActivity

class SettingsFragment : VerticalGridSupportFragment() {

    companion object {
        private const val COLUMNS = 4
        private const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

        fun getInstance() = SettingsFragment()
    }

    private var mAdapter: ArrayObjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.title_fragment_settings)
        setupRowAdapter()
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(ZOOM_FACTOR)
        gridPresenter.numberOfColumns = COLUMNS
        setGridPresenter(gridPresenter)
        val cardPresenter = SettingsIconPresenter(requireContext()) { card: Card.SettingsCard ->
            when (card.type) {
                Card.SettingsCard.Type.ETA -> {
                    startActivity(
                        Intent(context, EditEtaActivity::class.java)
                    )
                }
                Card.SettingsCard.Type.TRAFFIC -> {
                }
                Card.SettingsCard.Type.NONE -> {
                }
                Card.SettingsCard.Type.VERSION -> {
                }
                Card.SettingsCard.Type.ETA_LAYOUT -> {
                    startActivity(
                        Intent(context, EtaLayoutSelectionActivity::class.java)
                    )
                }
            }
        }
        mAdapter = ArrayObjectAdapter(cardPresenter)
        adapter = mAdapter
        createRows()
    }

    private fun createRows() {
        val list = arrayListOf<Card.SettingsCard>()
        list.add(
            Card.SettingsCard(
                title = getString(R.string.title_activity_settings_eta),
                type = Card.SettingsCard.Type.ETA,
                icon = R.drawable.ic_settings_eta_listing_24
            )
        )
        list.add(
            Card.SettingsCard(
                title = getString(R.string.title_activity_eta_layout),
                type = Card.SettingsCard.Type.ETA_LAYOUT,
                icon = R.drawable.ic_settings_eta_layout_24
            )
        )
        list.add(
            Card.SettingsCard(
                title = getString(R.string.title_settings_version_leanback,
                    BuildConfig.VERSION_NAME),
                type = Card.SettingsCard.Type.VERSION,
                icon = R.drawable.ic_settings_version_24
            )
        )

        mAdapter?.addAll(0, list)
    }
}