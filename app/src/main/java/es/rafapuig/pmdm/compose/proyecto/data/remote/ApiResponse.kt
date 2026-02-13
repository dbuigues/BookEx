package es.rafapuig.pmdm.compose.proyecto.data.remote

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): ApiResponse<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error("Empty response body")
        } else {
            ApiResponse.Error(
                message = response.errorBody()?.string() ?: "Unknown error",
                code = response.code()
            )
        }
    } catch (e: Exception) {
        ApiResponse.Error(
            message = e.message ?: "Network error occurred"
        )
    }
}
