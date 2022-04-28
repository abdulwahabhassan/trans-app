package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class LoginRequest(
    val email: String,
    val password: String,
    @Json(name = "app_name")
    val appName: String = "v1_mobile"
)
