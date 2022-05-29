package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.VehicleCurrentEntity
import ng.gov.imostate.database.entity.VehiclePreviousEntity
import ng.gov.imostate.database.entity.VehicleRouteEntity

@Dao
interface VehicleLocalDao {

    @Query("SELECT * FROM vehicle_previous WHERE vehicle_plates = :identifier")
    suspend fun getVehicleDriverRecordFromPreviousEnumeration(identifier: String): VehiclePreviousEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehiclesFromPreviousEnumeration(vehicles: List<VehiclePreviousEntity>)

    @Query("SELECT id FROM vehicle_previous ORDER BY id DESC LIMIT 1")
    suspend fun getLastVehicleIdFromPreviousEnumeration(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVehiclesFromCurrentEnumeration(vehicles: List<VehicleCurrentEntity>)

    @Query("SELECT * FROM vehicle_current WHERE vehicle_plates = :identifier")
    suspend fun getVehicleFromCurrentEnumeration(identifier: String): VehicleCurrentEntity?

}
