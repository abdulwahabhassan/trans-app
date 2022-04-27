package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class OnboardVehicleRequest(
    @Json(name = "vehicle_plates")
    val vehiclePlates: String,
    val brand: String,
    val type: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "middle_name")
    val middleName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "d_o_b")
    val dOB: String,
    val address: String,
    val imssin: String,
    val email: String,
    val phone: String
)
