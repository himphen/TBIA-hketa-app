package hibernate.v2.sunshine.api

import hibernate.v2.api.model.Bound
import hibernate.v2.api.model.hko.TodayForecast
import hibernate.v2.api.model.hko.TodayWeather
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.api.response.EtaResponse
import hibernate.v2.api.response.RouteListResponse
import hibernate.v2.api.response.RouteResponse
import hibernate.v2.api.response.RouteStopListResponse
import hibernate.v2.api.response.StopListResponse
import hibernate.v2.api.response.StopResponse
import hibernate.v2.sunshine.BuildConfig

class DataRepository(private val apiManager: ApiManager) : BaseRepository() {

    suspend fun getStopEta(stopId: String, route: String): EtaResponse {
        return apiManager.etaService.getStopEta(
            stopId = stopId,
            route = route,
            serviceType = 1
        )
    }

    suspend fun getStop(stopId: String): StopResponse {
        return apiManager.etaService.getStop(
            stopId = stopId
        )
    }

    suspend fun getRoute(
        routeId: String,
        bound: Bound,
        serviceType: String
    ): RouteResponse {
        return apiManager.etaService.getRoute(
            route = routeId,
            bound = bound.value,
            serviceType = serviceType
        )
    }

    suspend fun getRouteList(): RouteListResponse {
        return apiManager.etaService.getRouteList()
    }

    suspend fun getStopList(): StopListResponse {
        return apiManager.etaService.getStopList()
    }

    suspend fun getRouteStopList(): RouteStopListResponse {
        return apiManager.etaService.getRouteStopList()
    }

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
