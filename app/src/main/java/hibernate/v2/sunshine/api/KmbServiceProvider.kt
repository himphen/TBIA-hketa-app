package hibernate.v2.sunshine.api

import com.google.gson.Gson
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.KmbService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KmbServiceProvider(val apiManager: ApiManager) {
    var kmbService: KmbService? = null

    fun getService(): KmbService {
        kmbService?.let {
            return it
        } ?: run {
            val service = Retrofit.Builder()
                .baseUrl("https://data.etabus.gov.hk/")
                .client(apiManager.client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ApiConverterFactory(Gson()))
                .build()
                .create(KmbService::class.java)

            kmbService = service

            return service
        }
    }
}