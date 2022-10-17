package hibernate.v2.sunshine.api

import com.google.gson.Gson
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.HkoWeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HkoWeatherServiceProvider(val apiManager: ApiManager) {
    private var hkoWeatherService: HkoWeatherService? = null

    fun getService(): HkoWeatherService {
        hkoWeatherService?.let {
            return it
        } ?: run {
            val service = Retrofit.Builder()
                .baseUrl("https://data.weather.gov.hk/")
                .client(apiManager.client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ApiConverterFactory(Gson()))
                .build().create(HkoWeatherService::class.java)

            hkoWeatherService = service

            return service
        }
    }
}