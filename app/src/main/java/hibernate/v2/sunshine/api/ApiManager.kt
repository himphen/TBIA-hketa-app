package hibernate.v2.sunshine.api

import android.util.Patterns
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.DataService
import hibernate.v2.api.service.GmbService
import hibernate.v2.api.service.HkoWeatherService
import hibernate.v2.api.service.KmbService
import hibernate.v2.api.service.TransportService
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.util.JsonUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class ApiManager {
    private val connectTimeout: Long = 15
    private val readTimeout: Long = 15
    private val writeTimeout: Long = 15
    private lateinit var client: OkHttpClient
    lateinit var dataService: DataService
    lateinit var kmbService: KmbService
    lateinit var gmbService: GmbService
    lateinit var transportService: TransportService
    lateinit var hkoWeatherService: HkoWeatherService

    private fun initClient() {
        // init okHttp Client
        client = OkHttpClient().newBuilder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addNetworkInterceptor(
                HttpLoggingInterceptor(HttpLogger()).apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
                }
            )
            .addInterceptor(ApiLogInterceptor())
            .build()

        var dataServiceBaseUrl = Firebase.remoteConfig.getString("api_base_url_v1")

        if (!Patterns.WEB_URL.matcher(dataServiceBaseUrl).matches()) {
            dataServiceBaseUrl = "https://localhost/"
        }

        dataService = Retrofit.Builder()
            .baseUrl(dataServiceBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build()
            .create(DataService::class.java)

        kmbService = Retrofit.Builder()
            .baseUrl("https://data.etabus.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build()
            .create(KmbService::class.java)

        transportService = Retrofit.Builder()
            .baseUrl("https://rt.data.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build()
            .create(TransportService::class.java)

        hkoWeatherService = Retrofit.Builder()
            .baseUrl("https://data.weather.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(HkoWeatherService::class.java)

        gmbService = Retrofit.Builder()
            .baseUrl("https://data.etagmb.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(GmbService::class.java)
    }

    init {
        initClient()
    }

    // TODO refactor later
    fun reInitClient() {
        initClient()
    }
}

class HttpLogger : HttpLoggingInterceptor.Logger {
    private var mMessage: StringBuffer = StringBuffer("")

    override fun log(message: String) {
        var tempMessage = message
        if (message.startsWith("--> POST") ||
            message.startsWith("--> GET") ||
            message.startsWith("--> PATCH") ||
            message.startsWith("--> PUT")
        ) {
            mMessage.setLength(0)
        }
        if (message.startsWith("{") && message.endsWith("}") ||
            message.startsWith("[") && message.endsWith("]")
        ) {
            tempMessage = JsonUtils.formatJson(message)
        }
        mMessage.append(tempMessage + "\n")

        if (message.startsWith("<-- END HTTP")) {
            Logger.log(Logger.INFO, "api", mMessage.toString(), null)
        }
    }
}
