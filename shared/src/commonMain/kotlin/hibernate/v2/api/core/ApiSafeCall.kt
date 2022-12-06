package hibernate.v2.api.core

import hibernate.v2.api.response.ErrorResponse
import hibernate.v2.utils.HttpCustomException
import hibernate.v2.utils.KtorUnknownHostException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse

object ApiSafeCall {
    suspend operator fun <T> invoke(
        apiCall: suspend () -> T
    ): Resource<T> {
        return try {
            val result = apiCall.invoke()
            Resource.Success(result)
        } catch (throwable: Throwable) {
            @Suppress("USELESS_IS_CHECK")
            when (throwable) {
                is ServerResponseException -> {
                    val code = throwable.response.status.value
                    val errorResponse =
                        getErrorResponse(throwable.response)
                    Resource.HttpError(
                        code,
                        HttpCustomException(
                            throwable,
                            HttpCustomException.Error.SERVER_ERROR,
                            errorResponse,
                            _code = 500
                        ),
                        errorResponse
                    )
                }
                is ClientRequestException -> {
                    val code = throwable.response.status.value
                    val errorResponse =
                        getErrorResponse(throwable.response)
                    Resource.HttpError(
                        code,
                        HttpCustomException(
                            throwable,
                            HttpCustomException.Error.CLIENT_ERROR,
                            errorResponse,
                            _code = 400
                        ),
                        errorResponse
                    )
                }
                is KtorUnknownHostException -> {
                    Resource.HttpError(
                        900,
                        HttpCustomException(
                            throwable,
                            HttpCustomException.Error.LOST_CONNECTION,
                            null,
                            _code = 900
                        ),
                    )
                }
                else -> {
                    Resource.OtherError(throwable)
                }
            }
        }
    }

    private suspend fun getErrorResponse(response: HttpResponse): ErrorResponse {
        return try {
            response.body()
        } catch (exception: Exception) {
            ErrorResponse.getEmptyErrorResponse()
        }
    }
}
