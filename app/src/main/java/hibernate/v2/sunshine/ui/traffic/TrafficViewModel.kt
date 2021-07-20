package hibernate.v2.sunshine.ui.traffic

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLngBounds
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel

class TrafficViewModel(private val repo: WeatherRepository) : BaseViewModel() {
    val locationList = MutableLiveData<List<LatLngBounds>>()
}
