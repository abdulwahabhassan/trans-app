package ng.gov.imostate.model.response

import com.squareup.moshi.Json

data class OnboardRoute(
    val id: Long?,
    @Json(name = "route_id")
    val routeID: Long?,
    @Json(name = "vehicle_id")
    val vehicleID: Long?,
    @Json(name = "driver_id")
    val driverID: Long?,
    val status: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)
