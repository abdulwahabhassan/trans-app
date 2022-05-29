package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class VehicleRoute(
    val id: Long? = null,
    @Json(name = "route_id")
    val routeID: Long? = null,
    val from: String? = null,
    val to: String? = null,
    @Json(name = "vehicle_id")
    val vehicleID: Long? = null,
    @Json(name = "driver_id")
    val driverID: Long? = null,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null,
    var selected: Boolean = false
)