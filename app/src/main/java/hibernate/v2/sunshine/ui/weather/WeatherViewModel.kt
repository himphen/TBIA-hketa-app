package hibernate.v2.sunshine.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class WeatherViewModel(private val repo: WeatherRepository) : BaseViewModel() {
    val oneCallData = MutableLiveData<OneCall>()

    fun getWeatherInfo() {
        Logger.t("lifecycle").d("getWeatherInfo")
        viewModelScope.launch {
            repo.todayWeather()
        }
    }
}
