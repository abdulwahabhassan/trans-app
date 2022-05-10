package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey val id: Long? = null,
    @SerializedName("route_id")
    @ColumnInfo(name = "route_id")
    val routeID: Long? = null,
    @SerializedName("vehicle_id")
    @ColumnInfo(name = "vehicle_id")
    val vehicleID: Long? = null,
    @SerializedName("driver_id")
    @ColumnInfo(name = "driver_id")
    val driverID: Long? = null,
    @ColumnInfo(name = "status")
    val status: String? = null,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)