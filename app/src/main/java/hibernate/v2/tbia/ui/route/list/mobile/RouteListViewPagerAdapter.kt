package hibernate.v2.tbia.ui.route.list.mobile

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.icerock.moko.graphics.colorInt
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.TabAddEtaBinding
import hibernate.v2.utils.localized

class RouteListViewPagerAdapter(
    private val fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    val list = listOf(
        EtaType.KMB,
        EtaType.NWFB,
        EtaType.CTB,
        EtaType.NLB,
        EtaType.GMB_HKI,
        EtaType.GMB_KLN,
        EtaType.GMB_NT,
        EtaType.MTR,
        EtaType.LRT,
    )

    /**
     * Returns the number of pages
     */
    override fun getItemCount(): Int = list.size

    /**
     * This method will be invoked when a page is requested to create
     */
    override fun createFragment(position: Int): Fragment {
        return RouteListRouteFragment.getInstance(list[position])
    }

    fun getTabView(position: Int): View {
        val context = fragment.requireContext()
        val etaType = list[position]
        val viewBinding = TabAddEtaBinding.inflate(LayoutInflater.from(context))
        viewBinding.tabTitleTv.text = etaType.name().localized(context)

        when (etaType) {
            EtaType.KMB,
            EtaType.NWFB,
            EtaType.CTB,
            EtaType.NLB -> ContextCompat.getDrawable(context, R.drawable.ic_bus_24)
            EtaType.GMB_HKI,
            EtaType.GMB_KLN,
            EtaType.GMB_NT -> ContextCompat.getDrawable(context, R.drawable.ic_bus_mini_24)
            EtaType.MTR -> ContextCompat.getDrawable(context, R.drawable.ic_train_24)
            EtaType.LRT -> ContextCompat.getDrawable(context, R.drawable.ic_tram_24)
        }?.let {
            it.mutate()
            viewBinding.tabTitleTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                it,
                null,
                null,
                null
            )
            viewBinding.tabTitleTv.compoundDrawablesRelative[0]?.setTint(
                etaType.color().colorInt()
            )
        }

        return viewBinding.root
    }
}
