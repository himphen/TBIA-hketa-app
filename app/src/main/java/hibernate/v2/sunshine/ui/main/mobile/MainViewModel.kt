package hibernate.v2.sunshine.ui.main.mobile

import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel : BaseViewModel() {

    val onUpdatedEtaList = MutableSharedFlow<Unit>()
    val onUpdatedEtaLayout = MutableSharedFlow<Unit>()

    val onRequestedCloseBottomSheet = MutableSharedFlow<Unit>()

    val onRouteBottomSheetStateChanged = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)
    val onStopBottomSheetStateChanged = MutableLiveData(BottomSheetBehavior.STATE_HIDDEN)

    val selectedTab = MutableLiveData<MainViewPagerAdapter.TabType>()

    fun isBottomSheetClosed(): Boolean {
        return onRouteBottomSheetStateChanged.value == BottomSheetBehavior.STATE_HIDDEN
                && onStopBottomSheetStateChanged.value == BottomSheetBehavior.STATE_HIDDEN

    }
}