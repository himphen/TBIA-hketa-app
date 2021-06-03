package hibernate.v2.sunshine.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repo: DataRepository) : BaseViewModel() {
    val oneCallData = MutableLiveData<OneCall>()

    fun getWeatherInfo() {
        Logger.d("lifecycle getWeatherInfo")
        viewModelScope.launch {
            try {
                launch {
                    oneCallData.postValue(repo.currentWeather(22.32215, 114.16994))
                }
            } catch (e: Exception) {
                Logger.e(e, "net error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
