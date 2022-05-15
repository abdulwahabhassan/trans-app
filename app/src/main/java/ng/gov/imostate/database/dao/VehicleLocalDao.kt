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

    @Query("SELECT * FROM vehicle WHERE vehicle_plates = :identifier")
    suspend fun getVehicleDriverRecord(identifier: String): VehicleEntity?

    @Query("SELECT * FROM vehicle")
    suspend fun getAllVehicles(): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehicles(vehicles: List<VehicleEntity>)

}
