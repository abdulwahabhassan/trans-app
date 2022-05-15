package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Transaction (
    val id: Long,
    @Json(name = "from_vehicle_id")
    val fromVehicleID: Long,
    @Json(name = "from_user_id")
    val fromUserID: Any? = null,
    @Json(name = "external_reference")
    val externalReference: Any? = null,
    @Json(name = "internal_reference")
    val internalReference: String,
    @Json(name = "account_from")
    val accountFrom: String,
    @Json(name = "account_to")
    val accountTo: String,
    @Json(name = "to_id")
    val toID: Long,
    val amount: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "vehicle_from")
    val vehicleFrom: Vehicle
)
