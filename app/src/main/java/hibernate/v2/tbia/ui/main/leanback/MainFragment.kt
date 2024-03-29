package hibernate.v2.tbia.ui.main.leanback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hibernate.v2.tbia.databinding.LbFragmentMainBinding
import hibernate.v2.tbia.ui.base.BaseFragment
import hibernate.v2.tbia.ui.bookmark.home.leanback.BookmarkHomeFragment

class MainFragment : BaseFragment<LbFragmentMainBinding>() {

    companion object {
        fun getInstance() = MainFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LbFragmentMainBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(viewBinding!!.containerTopLeft.id, BookmarkHomeFragment.getInstance())
            .commit()
//        childFragmentManager.beginTransaction()
//            .replace(viewBinding!!.containerTopRight.id, TrafficFragment.getInstance())
//            .commit()
//        childFragmentManager.beginTransaction()
//            .replace(viewBinding!!.containerBottom.id, WeatherFragment.getInstance())
//            .commit()
    }
}
