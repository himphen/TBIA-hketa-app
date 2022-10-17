package hibernate.v2.sunshine.repository

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.sunshine.api.HkoWeatherServiceProvider

class WeatherRepository(
    private val hkoWeatherServiceProvider: HkoWeatherServiceProvider
) : BaseRepository() {
    suspend fun todayWeather(): TodayWeather {
        val result = ApiSafeCall {
            hkoWeatherServiceProvider.getService().todayWeather()
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
            hkoWeatherServiceProvider.getService().todayForecast()
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return data
    }
}
