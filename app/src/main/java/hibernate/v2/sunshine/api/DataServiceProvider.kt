package hibernate.v2.sunshine.api

import android.util.Patterns
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.DataService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataServiceProvider(val apiManager: ApiManager) {
    var dataService: DataService? = null

    fun getService(): DataService {
        var dataServiceBaseUrl = Firebase.remoteConfig.getString("api_base_url_v1")

        if (!Patterns.WEB_URL.matcher(dataServiceBaseUrl).matches()) {
            dataServiceBaseUrl = "https://localhost/"
        }

        dataService?.let {
            return it
        } ?: run {
            val service = Retrofit.Builder()
                .baseUrl(dataServiceBaseUrl)
                .client(apiManager.client)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ApiConverterFactory(Gson()))
                .build()
                .create(DataService::class.java)

            dataService = service

            return service
        }
    }
}