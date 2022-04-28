package ng.gov.imostate.model.response

import com.squareup.moshi.Json

data class ApiErrorResponse(
    val success: Boolean,
    val message: String?,
    val errors: Map<String, String>?,
    @Json(name = "error_code")
    val errorCode: Long?
) : Response
