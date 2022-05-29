package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.VehicleRouteEntity

@Dao
interface RouteLocalDao {
    @Query("SELECT * FROM vehicle_route")
    suspend fun getAllRoutes(): List<VehicleRouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutes(rates: List<VehicleRouteEntity>)

}