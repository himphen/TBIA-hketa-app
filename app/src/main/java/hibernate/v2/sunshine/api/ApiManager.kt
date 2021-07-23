package hibernate.v2.sunshine.api

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.himphen.logger.Logger
import hibernate.v2.api.core.ApiConverterFactory
import hibernate.v2.api.service.GmbService
import hibernate.v2.api.service.HkoWeatherService
import hibernate.v2.api.service.KmbService
import hibernate.v2.api.service.NCService
import hibernate.v2.api.service.OpenWeatherService
import hibernate.v2.api.service.TrafficService
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.util.JsonUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class ApiManager(val context: Context) {
    private val connectTimeout: Long = 15
    private val readTimeout: Long = 15
    private val writeTimeout: Long = 15
    private lateinit var client: OkHttpClient
    lateinit var trafficService: TrafficService
    lateinit var kmbService: KmbService
    lateinit var gmbService: GmbService
    lateinit var ncService: NCService
    lateinit var hkoWeatherService: HkoWeatherService
    lateinit var openWeatherService: OpenWeatherService

    private fun initClient() {
        // init okHttp Client
        client = OkHttpClient().newBuilder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor(HttpLogger()).apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            })
            .addInterceptor(ApiLogInterceptor())
            .build()

        kmbService = Retrofit.Builder()
            .baseUrl("https://data.etabus.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build()
            .create(KmbService::class.java)

        ncService = Retrofit.Builder()
            .baseUrl("https://rt.data.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build()
            .create(NCService::class.java)

        hkoWeatherService = Retrofit.Builder()
            .baseUrl("https://data.weather.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(HkoWeatherService::class.java)

        openWeatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(OpenWeatherService::class.java)

        gmbService = Retrofit.Builder()
            .baseUrl("https://data.etagmb.gov.hk/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(GmbService::class.java)

        trafficService = Retrofit.Builder()
            .baseUrl("https://static.data.gov.hk/td/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ApiConverterFactory(Gson()))
            .build().create(TrafficService::class.java)
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
