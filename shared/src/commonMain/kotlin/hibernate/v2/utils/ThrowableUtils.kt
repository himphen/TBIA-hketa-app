package hibernate.v2.utils

import hibernate.v2.MR
import hibernate.v2.api.response.ErrorResponse

class KtorThrowable(
    val error: Error?,
    val errorResponse: ErrorResponse? = null,
    private val _code: Int?,
) : Throwable() {

    fun getCode(): Int {
        return _code ?: 700
    }

    enum class Error {
        LOST_CONNECTION,
        SERVER_ERROR,
        CLIENT_ERROR,
        OTHER
    }
}

fun getEtaUpdateErrorMessage(throwable: Throwable, context: KMMContext): String {
    if (throwable is KtorThrowable) {
        return when (throwable.error) {
            KtorThrowable.Error.LOST_CONNECTION ->
                MR.strings.text_eta_loading_ktor_lost_connection.localized(context)
            KtorThrowable.Error.SERVER_ERROR ->
                MR.strings.text_eta_loading_ktor_server_error.localized(
                    context,
                    listOf(throwable.getCode())
                )
            KtorThrowable.Error.CLIENT_ERROR ->
                MR.strings.text_eta_loading_ktor_client_error.localized(
                    context,
                    listOf(throwable.getCode())
                )
            KtorThrowable.Error.OTHER,
            null ->
                MR.strings.text_eta_loading_ktor_other_error.localized(
                    context,
                    listOf(throwable.getCode())
                )
        }
    }

    return MR.strings.text_eta_loading_ktor_other_error.localized(
        context,
        listOf(700)
    )
}