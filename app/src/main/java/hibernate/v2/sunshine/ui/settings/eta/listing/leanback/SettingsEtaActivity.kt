package hibernate.v2.sunshine.ui.settings.eta.listing.leanback

import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.databinding.LbActivityContainerBinding
import hibernate.v2.sunshine.ui.base.BaseLeanbackActivity

class SettingsEtaActivity : BaseLeanbackActivity<LbActivityContainerBinding>() {
    override fun getActivityViewBinding() = LbActivityContainerBinding.inflate(layoutInflater)
    override var fragment: Fragment? = SettingsEtaFragment.getInstance()
}