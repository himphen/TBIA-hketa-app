package hibernate.v2.sunshine.ui.main.mobile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.bookmark.home.mobile.BookmarkHomeFragment
import hibernate.v2.sunshine.ui.searchmap.SearchMapFragment
import hibernate.v2.sunshine.ui.settings.mobile.SettingsFragment

class MainViewPagerAdapter(
    activity: MainActivity
) : FragmentStateAdapter(activity) {

    enum class TabType(val position: Int, val menuItemId: Int) {
        Eta(0, R.id.menu_home),
        SearchMap(1, R.id.menu_search_map),
        Settings(2, R.id.menu_settings);

        companion object {
            fun fromMenuItemId(menuItemId: Int?) = values().find { it.menuItemId == menuItemId }
            fun fromPosition(position: Int?) = values().find { it.position == position }
        }
    }

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = 3

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BookmarkHomeFragment.getInstance()
            1 -> SearchMapFragment.getInstance()
            else -> SettingsFragment()
        }
    }
}
