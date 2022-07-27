package hibernate.v2.sunshine.repository

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.sunshine.api.ApiManager

class WeatherRepository(private val apiManager: ApiManager) : BaseRepository() {
    suspend fun todayWeather(): TodayWeather {
        val result = ApiSafeCall {
            apiManager.hkoWeatherService.todayWeather()
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return data
    }

    suspend fun todayForecast(): TodayForecast {
        val result = ApiSafeCall {
            apiManager.hkoWeatherService.todayForecast()
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return data
    }
}
