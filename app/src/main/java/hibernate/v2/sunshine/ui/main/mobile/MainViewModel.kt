package hibernate.v2.sunshine.ui.main.mobile

import androidx.lifecycle.MutableLiveData
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel : BaseViewModel() {

    val onUpdatedEtaList = MutableSharedFlow<Unit>()
    val onUpdatedEtaLayout = MutableSharedFlow<Unit>()
    val selectedTab = MutableLiveData<MainViewPagerAdapter.TabType>()

}