package hibernate.v2.sunshine.api

import android.content.Context
import com.himphen.logger.Logger
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.util.JsonUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApiManager(val context: Context) {
    companion object {
        private const val CONNECT_TIMEOUT: Long = 15
        private const val READ_TIMEOUT: Long = 15
        private const val WRITE_TIMEOUT: Long = 15
//        private const val CACHE_SIZE_BYTES = 1024 * 1024 * 2L
    }

    val client: OkHttpClient = OkHttpClient().newBuilder()
//        .cache(Cache(context.cacheDir, CACHE_SIZE_BYTES))
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .addNetworkInterceptor(
            HttpLoggingInterceptor(HttpLogger()).apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
            }
        )
        .addInterceptor(ApiLogInterceptor())
        .build()
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
