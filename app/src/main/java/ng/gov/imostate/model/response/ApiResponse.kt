package ng.gov.imostate.model.response

data class ApiResponse<Any>(
    var status: Boolean = false,
    var message: String? = null,
    val data: Any? = null
)