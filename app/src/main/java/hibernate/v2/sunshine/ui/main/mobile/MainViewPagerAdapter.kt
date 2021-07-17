package hibernate.v2.sunshine.ui.main.mobile

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.eta.mobile.EtaFragment

class MainViewPagerAdapter(
    private val fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val tabTitles: Array<String> = fragment.resources.getStringArray(R.array.main_tab_title)

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = 1

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EtaFragment()
            else -> EtaFragment()
        }
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(fragment.context, R.layout.custom_tab, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = tabTitles[position]
        return v
    }

}