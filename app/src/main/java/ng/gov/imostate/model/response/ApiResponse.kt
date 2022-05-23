package ng.gov.imostate.model.response

import ng.gov.imostate.model.domain.Meta

data class ApiResponse<T>(
    var success: Boolean = false,
    var message: String? = null,
    val result: T? = null,
    val meta: Meta? = null
)