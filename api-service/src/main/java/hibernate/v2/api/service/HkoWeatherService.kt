package hibernate.v2.api.service

import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import retrofit2.http.GET

interface HkoWeatherService {
    @GET("weatherAPI/opendata/weather.php?dataType=flw&lang=tc")
    suspend fun todayWeather(): TodayWeather

    @GET("weatherAPI/opendata/weather.php?dataType=rhrread&lang=tc")
    suspend fun todayForecast(): TodayForecast
}
