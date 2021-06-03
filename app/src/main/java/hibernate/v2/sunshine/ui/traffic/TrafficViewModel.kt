package hibernate.v2.sunshine.ui.traffic

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLngBounds
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.model.RouteEtaStop
import hibernate.v2.sunshine.ui.base.BaseViewModel

class TrafficViewModel(private val repo: DataRepository) : BaseViewModel() {
    val locationList = MutableLiveData<List<LatLngBounds>>()
}
