package hibernate.v2.api.core

import com.google.gson.Gson
import hibernate.v2.api.response.ErrorResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object ApiSafeCall {
    suspend operator fun <T> invoke(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(dispatcher) {
            try {
                val result = apiCall.invoke()
                if (result != null) {
                    Resource.Success(result as T)
                } else {
                    Resource.OtherError(NetworkThrowable.BodyParsing.throwable)
                }

            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> Resource.OtherError(throwable)
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse =
                            getErrorResponse(throwable) ?: ErrorResponse.getEmptyErrorResponse()
                        Resource.HttpError(
                            code,
                            NetworkErrorHelper.getThrowable(code).throwable,
                            errorResponse
                        )
                    }
                    else -> {
                        Resource.OtherError(throwable)
                    }
                }
            }
        }
    }

    private fun getErrorResponse(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.string()?.let {
                Gson().fromJson(it, ErrorResponse::class.java)
            }
        } catch (exception: Exception) {
            null
        }
    }

    enum class NetworkThrowable(val throwable: Throwable) {
        InformationalError(Throwable("[Http] Informational error, code 1XX")),
        RedirectionError(Throwable("[Http] Redirection error, code 3XX")),
        OtherClientError(Throwable("[Http] Other client error, code 4XX")),
        OtherServerError(Throwable("[Http] Other server error, code 5XX")),
        NotModified(Throwable("[Http] Not modified, code 304")),
        BadRequest(Throwable("[Http] Bad request, code 400")),
        Unauthorized(Throwable("[Http] Unauthorized, code 401")),
        Forbidden(Throwable("[Http] Forbidden, code 403")),
        NotFound(Throwable("[Http] Not found, code 404")),
        InternalServerError(Throwable("[Http] Internal server error, code 500")),
        ServiceUnavailable(Throwable("[Http] Service unavailable, code 503")),
        GatewayTimeout(Throwable("[Http] Gateway timeout, code 504")),
        Undefined(Throwable("[Http] Undefined, code 0")),
        BodyParsing(Throwable("[Parsing] HTTP Status in Successful, but failed to parse the response")),
        JSONParsing(Throwable("[JsonSyntaxException] failed to parse the JSON response")),
    }

    object NetworkErrorHelper {
        //300
        private const val NOT_MODIFIED = 304

        //400
        private const val BAD_REQUEST = 400
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404

        //500
        private const val INTERNAL_SERVER_ERROR = 500
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504


        fun getThrowable(errorCode: Int): NetworkThrowable {
            when (errorCode) {
                NOT_MODIFIED -> return NetworkThrowable.NotModified
                BAD_REQUEST -> return NetworkThrowable.BadRequest
                UNAUTHORIZED -> return NetworkThrowable.Unauthorized
                FORBIDDEN -> return NetworkThrowable.Forbidden
                NOT_FOUND -> return NetworkThrowable.NotFound
                INTERNAL_SERVER_ERROR -> return NetworkThrowable.InternalServerError
                SERVICE_UNAVAILABLE -> return NetworkThrowable.ServiceUnavailable
                GATEWAY_TIMEOUT -> return NetworkThrowable.GatewayTimeout


                in 100..199 -> return NetworkThrowable.InformationalError
                in 300..399 -> return NetworkThrowable.RedirectionError
                in 400..499 -> return NetworkThrowable.OtherClientError
                in 500..600 -> return NetworkThrowable.OtherServerError


                else -> {
                    return NetworkThrowable.Undefined
                }
            }
        }
    }
}
