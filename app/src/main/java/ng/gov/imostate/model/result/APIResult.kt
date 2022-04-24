package ng.gov.imostate.model.result

sealed class APIResult<out T> {
    data class Success<out T>(val value: T) : APIResult<T>()
    data class Error(val message: String? = null) :
        APIResult<Nothing>()
}
