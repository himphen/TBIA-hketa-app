package hibernate.v2.sunshine.ui.settings.eta.add.mobile

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel

class AddEtaViewPagerAdapter(
    private val fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    val list = listOf(
        AddEtaViewModel.EtaType.KMB,
        AddEtaViewModel.EtaType.NWFB_CTB,
        AddEtaViewModel.EtaType.GMB
    )

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = list.size

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return AddEtaRouteFragment.getInstance(list[position])
    }

    fun getTabView(position: Int): View {
        val v = View.inflate(fragment.context, R.layout.tab_add_eta, null)
        v.findViewById<TextView>(R.id.tabTitleTv).text = list[position].etaTypeName(v.context)
        return v
    }
}