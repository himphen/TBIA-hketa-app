package hibernate.v2.sunshine.api

import com.google.gson.Gson
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.GmbService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GmbServiceProvider(val apiManager: ApiManager) {
    private var gmbService: GmbService? = null

    fun getService(): GmbService {
        gmbService?.let {
            return it
        } ?: run {
            val service = Retrofit.Builder()
                .baseUrl("https://data.etagmb.gov.hk/")
                .client(apiManager.client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ApiConverterFactory(Gson()))
                .build()
                .create(GmbService::class.java)

            gmbService = service

            return service
        }
    }
}