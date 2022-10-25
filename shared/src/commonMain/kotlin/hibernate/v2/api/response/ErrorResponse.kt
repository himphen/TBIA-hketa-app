package hibernate.v2.api.response

data class ErrorResponse(
    val status: String = "",
    val error: String = "",
    val message: String = ""
) {
    companion object {
        fun getEmptyErrorResponse(): ErrorResponse {
            return ErrorResponse("", "", "")
        }
    }
}