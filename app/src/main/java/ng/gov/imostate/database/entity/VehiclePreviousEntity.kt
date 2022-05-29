package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import ng.gov.imostate.database.converters.Converters

@Entity(tableName = "vehicle_previous")
@TypeConverters(Converters::class)
data class VehiclePreviousEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: Long?,
    @ColumnInfo(name = "vehicle_plates")
    val vehiclePlates: String?,
    @ColumnInfo
    val brand: String?,
    @ColumnInfo
    val type: String?,
    @ColumnInfo(name = "fleet_number")
    val fleetNumber: String?,
    @ColumnInfo(name = "state_of_registration")
    val stateOfRegistration: String?,
    @ColumnInfo(name = "chasis_number")
    val chasisNumber: String?,
    @ColumnInfo(name = "engine_number")
    val engineNumber: String?,
    @SerializedName("vehicle_model")
    @ColumnInfo(name = "vehicle_model")
    val vehicleModel: String?,
    @ColumnInfo(name = "passenger_capacity")
    val passengerCapacity: String?,
    @ColumnInfo(name = "road_worthiness_exp_date")
    val roadWorthinessExpDate: String?,
    @ColumnInfo(name = "vehicle_licence_exp_date")
    val vehicleLicenceExpDate: String?,
    @ColumnInfo(name = "vehicle_insurance_exp_date")
    val vehicleInsuranceExpDate: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo
    val driver: DriverEntity?,
    @ColumnInfo(name = "vehicle_routes")
    val vehicleVehicleRoutes: List<VehicleRouteEntity>?
)