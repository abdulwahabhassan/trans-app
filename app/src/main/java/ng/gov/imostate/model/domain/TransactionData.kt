package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class TransactionData(
    @Json(name = "vehicle_id")
    val vehicleId: String?,
    @Json(name = "to")
    val to: String?
)
