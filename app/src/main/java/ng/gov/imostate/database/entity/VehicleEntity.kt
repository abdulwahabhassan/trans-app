package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import ng.gov.imostate.database.converters.Converters

@Entity(tableName = "vehicle")
@TypeConverters(Converters::class)
data class VehicleEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: Long?,
    @SerializedName("vehicle_plates")
    @ColumnInfo(name = "vehicle_plates")
    val vehiclePlates: String?,
    @ColumnInfo
    val brand: String?,
    @ColumnInfo
    val type: String?,
    @SerializedName("fleet_number")
    @ColumnInfo(name = "fleet_number")
    val fleetNumber: String?,
    @SerializedName("state_of_registration")
    @ColumnInfo(name = "state_of_registration")
    val stateOfRegistration: String?,
    @SerializedName("chasis_number")
    @ColumnInfo(name = "chasis_number")
    val chasisNumber: String?,
    @SerializedName("engine_number")
    @ColumnInfo(name = "engine_number")
    val engineNumber: String?,
    @SerializedName("vehicle_model")
    @ColumnInfo(name = "vehicle_model")
    val vehicleModel: String?,
    @SerializedName("passenger_capacity")
    @ColumnInfo(name = "passenger_capacity")
    val passengerCapacity: String?,
    @SerializedName("road_worthiness_exp_date")
    @ColumnInfo(name = "road_worthiness_exp_date")
    val roadWorthinessExpDate: String?,
    @SerializedName("vehicle_licence_exp_date")
    @ColumnInfo(name = "vehicle_licence_exp_date")
    val vehicleLicenceExpDate: String?,
    @SerializedName("vehicle_insurance_exp_date")
    @ColumnInfo(name = "vehicle_insurance_exp_date")
    val vehicleInsuranceExpDate: String?,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo
    val driver: DriverEntity?,
    @ColumnInfo
    val routes: List<RouteEntity>?
)