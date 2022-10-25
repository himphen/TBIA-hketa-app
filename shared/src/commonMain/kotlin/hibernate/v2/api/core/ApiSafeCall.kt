package hibernate.v2.api.core

import hibernate.v2.api.response.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiSafeCall {
    suspend operator fun <T> invoke(
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(dispatcher) {
            try {
                val result = apiCall.invoke()
                Resource.Success(result)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is ServerResponseException -> {
                        val code = throwable.response.status.value
                        val errorResponse =
                            getErrorResponse(throwable.response)
                        Resource.HttpError(
                            code,
                            throwable,
                            errorResponse
                        )
                    }
                    is RedirectResponseException -> {
                        val code = throwable.response.status.value
                        val errorResponse =
                            getErrorResponse(throwable.response)
                        Resource.HttpError(
                            code,
                            throwable,
                            errorResponse
                        )
                    }
                    is ClientRequestException -> {
                        val code = throwable.response.status.value
                        val errorResponse =
                            getErrorResponse(throwable.response)
                        Resource.HttpError(
                            code,
                            throwable,
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

    private suspend fun getErrorResponse(response: HttpResponse): ErrorResponse {
        return try {
            response.body()
        } catch (exception: Exception) {
            ErrorResponse.getEmptyErrorResponse()
        }
    }
}
