package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleEntity

@Dao
interface DriverLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDrivers(drivers: List<DriverEntity>)

    @Query("SELECT * FROM driver")
    suspend fun getAllDrivers(): List<DriverEntity>
}