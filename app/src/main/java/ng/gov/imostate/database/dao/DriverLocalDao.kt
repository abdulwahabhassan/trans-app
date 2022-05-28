package ng.gov.imostate.database.dao

import androidx.room.*
import ng.gov.imostate.database.entity.DriverEntity

@Dao
interface DriverLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDrivers(drivers: List<DriverEntity>)

    @Query("SELECT * FROM driver")
    suspend fun getAllDrivers(): List<DriverEntity>

}