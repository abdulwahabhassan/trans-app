package ng.gov.imostate.model.response

data class ApiResponse<T>(
    var success: Boolean = false,
    var message: String? = null,
    val result: T? = null
)