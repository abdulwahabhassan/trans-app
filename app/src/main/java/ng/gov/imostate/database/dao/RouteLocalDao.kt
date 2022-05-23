package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.database.entity.RouteEntity

@Dao
interface RouteLocalDao {
    @Query("SELECT * FROM route")
    suspend fun getAllRoutes(): List<RouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutes(rates: List<RouteEntity>)

}