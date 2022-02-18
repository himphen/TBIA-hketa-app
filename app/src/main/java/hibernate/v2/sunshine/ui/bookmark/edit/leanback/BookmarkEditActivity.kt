package hibernate.v2.sunshine.ui.bookmark.edit.leanback

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.LbActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseLeanbackActivity

class BookmarkEditActivity : BaseLeanbackActivity<LbActivityContainerBinding>() {
    override fun getActivityViewBinding() = LbActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = BookmarkEditFragment.getInstance()
}
