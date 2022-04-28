package ng.gov.imostate.model.result

sealed class ApiResult<out T> {
    data class Success<out T>(val response: T) : ApiResult<T>()
    data class Error(val message: String? = null) :
        ApiResult<Nothing>()
}
