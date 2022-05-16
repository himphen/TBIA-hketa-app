package hibernate.v2.sunshine.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HkoWeatherViewModel(private val repo: WeatherRepository) : BaseViewModel() {
    val todayWeather = MutableLiveData<TodayWeather>()
    val todayForecast = MutableLiveData<TodayForecast>()

    fun getWeatherInfo() {
        Logger.t("lifecycle").d("getWeatherInfo")
        viewModelScope.launch {
            try {
                launch {
                    todayWeather.postValue(repo.todayWeather())
                }
                launch {
                    todayForecast.postValue(repo.todayForecast())
                }
            } catch (e: Exception) {
                Logger.e(e, "net error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
