package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class GetVehicleRequest(
    @Json(name = "vendorId")
    val vehicleLicense: String
)
