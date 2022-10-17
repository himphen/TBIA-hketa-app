package hibernate.v2.sunshine.api

import com.google.gson.Gson
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.TransportService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransportServiceProvider(val apiManager: ApiManager) {
    private var transportService: TransportService? = null

    fun getService(): TransportService {
        transportService?.let {
            return it
        } ?: run {
            val service = Retrofit.Builder()
                .baseUrl("https://rt.data.gov.hk/")
                .client(apiManager.client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ApiConverterFactory(Gson()))
                .build()
                .create(TransportService::class.java)

            transportService = service

            return service
        }
    }
}