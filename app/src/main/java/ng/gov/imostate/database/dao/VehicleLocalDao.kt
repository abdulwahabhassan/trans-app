package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleEntity

@Dao
interface VehicleLocalDao {

    @Query("SELECT * FROM vehicle WHERE vehicle_plates Like (:identifier)")
    suspend fun getVehicle(identifier: String): VehicleEntity?

    @Query(
        "SELECT vehicle.vehicle_plates AS identifier, " +
                "driver.first_name AS driverFirstName, " +
                "driver.last_name AS driverLastName, " +
                "vehicle.vehicle_licence_exp_date AS vehicleLicenseExpiryDate, " +
                "vehicle.type AS vehicleType " +
                "FROM vehicle, driver " +
                "WHERE vehicle.id = driver.vehicle_id AND vehicle.vehicle_plates LIKE :identifier "
    )
    suspend fun getVehicleDriverRecord(identifier: String): VehicleDriverRecord?

    @Query("SELECT * FROM vehicle")
    suspend fun getAllVehicles(): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehicles(vehicles: List<VehicleEntity>)

}

data class VehicleDriverRecord(
    val identifier: String?,
    val driverFirstName: String?,
    val driverLastName: String?,
    val vehicleType: String?,
    val vehicleLicenseExpiryDate: String?
)