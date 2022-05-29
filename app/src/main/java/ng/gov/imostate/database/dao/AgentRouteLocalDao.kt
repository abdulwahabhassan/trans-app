package ng.gov.imostate.database.dao

import androidx.room.*
import ng.gov.imostate.database.entity.AgentRouteEntity

@Dao
interface AgentRouteLocalDao {

    @Query("SELECT * FROM agent_route")
    suspend fun getAllAgentRoutes(): List<AgentRouteEntity>

    @Delete
    suspend fun deleteAllAgentRoutes(routes: List<AgentRouteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAgentRoutes(routes: List<AgentRouteEntity>)

}