package ng.gov.imostate.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey val id: Long? = null,
    @Json(name = "route_id")
    val routeID: Long? = null,
    @Json(name = "vehicle_id")
    val vehicleID: Long? = null,
    @Json(name = "driver_id")
    val driverID: Long? = null,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null
)