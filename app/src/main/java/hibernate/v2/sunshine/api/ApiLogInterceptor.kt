package hibernate.v2.sunshine.api

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response

class ApiLogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}


fun RequestBody?.bodyToString(): String {
    if (this == null) return ""
    val buffer = okio.Buffer()
    writeTo(buffer)
    return buffer.readUtf8()
}
