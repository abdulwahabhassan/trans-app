package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey val id: Long? = null,
    @ColumnInfo(name = "route_id")
    val routeID: Long? = null,
    @ColumnInfo(name = "vehicle_id")
    val vehicleID: Long? = null,
    @ColumnInfo(name = "driver_id")
    val driverID: Long? = null,
    @ColumnInfo(name = "status")
    val status: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)