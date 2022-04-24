package ng.gov.imostate.model.result

sealed class ViewModelResult<out R> {

    data class Success<out T>(val data: T) : ViewModelResult<T>()
    data class Error(val errorMessage: String) : ViewModelResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[errorMessage=$errorMessage]"
        }
    }
}