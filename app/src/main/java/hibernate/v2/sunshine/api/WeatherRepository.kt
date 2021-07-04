package hibernate.v2.sunshine.api

import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.sunshine.BuildConfig

class WeatherRepository(private val apiManager: ApiManager) : BaseRepository() {
    suspend fun todayWeather(): TodayWeather {
        return apiManager.hkoWeatherService.todayWeather()
    }

    suspend fun todayForecast(): TodayForecast {
        return apiManager.hkoWeatherService.todayForecast()
    }

    suspend fun currentWeather(lat: Double, lon: Double): OneCall {
        return apiManager.openWeatherService.oneCall(
            lat,
            lon,
            "",
            BuildConfig.OPEN_WEATHER_API_KEY,
            "metric"
        )
    }
}
