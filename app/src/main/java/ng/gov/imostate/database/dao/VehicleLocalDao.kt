package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.VehicleCurrentEntity
import ng.gov.imostate.database.entity.VehiclePreviousEntity

@Dao
interface VehicleLocalDao {

    @Query("SELECT * FROM vehicle_previous WHERE vehicle_plates = :identifier")
    suspend fun getVehicleDriverRecordFromPreviousEnumeration(identifier: String): VehiclePreviousEntity?

    @Query("SELECT * FROM vehicle_previous")
    suspend fun getAllVehiclesFromPreviousEnumeration(): List<VehiclePreviousEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehiclesFromPreviousEnumeration(vehiclePrevious: List<VehiclePreviousEntity>)

    @Query("SELECT id FROM vehicle_previous ORDER BY id DESC LIMIT 1")
    suspend fun getLastVehicleIdFromPreviousEnumerationInDatabase(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllVehiclesFromCurrentEnumeration(vehicleCurrent: List<VehicleCurrentEntity>)

}
