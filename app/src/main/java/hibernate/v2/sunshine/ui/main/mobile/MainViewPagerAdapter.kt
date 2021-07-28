package hibernate.v2.sunshine.ui.main.mobile

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.eta.mobile.EtaFragment
import hibernate.v2.sunshine.ui.searchmap.SearchMapFragment
import hibernate.v2.sunshine.ui.settings.mobile.SettingsFragment

class MainViewPagerAdapter(
    private val fragment: Fragment
) : FragmentStateAdapter(fragment) {

    enum class TabType(val position: Int, val menuItemId: Int) {
        Eta(0, R.id.home),
        SearchMap(1, R.id.searchMap),
        Settings(2, R.id.settings);

        companion object {
            fun fromMenuItemId(menuItemId: Int?) = values().find { it.menuItemId == menuItemId }
            fun fromPosition(position: Int?) = values().find { it.position == position }
        }
    }

    private val tabTitles: Array<String> = fragment.resources.getStringArray(R.array.main_tab_title)

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = 3

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EtaFragment()
            1 -> SearchMapFragment()
            else -> SettingsFragment()
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(fragment.context, R.layout.custom_tab, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }

}