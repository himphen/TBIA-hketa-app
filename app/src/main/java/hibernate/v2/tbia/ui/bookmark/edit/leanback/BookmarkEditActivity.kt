package hibernate.v2.tbia.ui.bookmark.edit.leanback

import androidx.fragment.app.Fragment
import hibernate.v2.tbia.databinding.LbActivityContainerBinding
import hibernate.v2.tbia.ui.base.BaseLeanbackActivity

class BookmarkEditActivity : BaseLeanbackActivity<LbActivityContainerBinding>() {
    override fun getActivityViewBinding() = LbActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = BookmarkEditFragment.getInstance()
}
