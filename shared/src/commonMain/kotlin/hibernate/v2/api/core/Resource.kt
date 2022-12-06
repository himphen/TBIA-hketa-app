package hibernate.v2.api.core

import hibernate.v2.api.response.ErrorResponse

sealed class Resource<out T>(
    val _data: T? = null,
    val code: Int? = null,
    val _throwable: Throwable? = null,
    val errorResponse: ErrorResponse? = null
) {
    class Success<T>(data: T) : Resource<T>(_data = data) {
        fun getData() = _data!!
    }

    class HttpError(
        code: Int? = null,
        throwable: Throwable? = null,
        errorResponse: ErrorResponse? = null
    ) : Resource<Nothing>(null, code, throwable, errorResponse) {
        fun getThrowable() = _throwable!!
    }

    class OtherError(throwable: Throwable? = null) : Resource<Nothing>(null, null, throwable) {
        fun getThrowable() = _throwable!!
    }
}