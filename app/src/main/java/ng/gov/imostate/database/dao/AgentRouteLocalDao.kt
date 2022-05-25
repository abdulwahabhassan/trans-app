package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.AgentRouteEntity
import ng.gov.imostate.database.entity.RouteEntity

@Dao
interface AgentRouteLocalDao {

    @Query("SELECT * FROM agent_route WHERE user_id=:userId")
    suspend fun getAllAgentRoutes(userId: Long): List<AgentRouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAgentRoutes(routes: List<AgentRouteEntity>)

}