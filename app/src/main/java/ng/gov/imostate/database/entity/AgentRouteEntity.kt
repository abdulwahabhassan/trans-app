package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_route")
data class AgentRouteEntity(
    val id: Long? = null,
    @ColumnInfo(name = "user_id")
    val userId: Long? = null,
    @PrimaryKey
    @ColumnInfo(name = "route_id")
    val routeId: Long? = null,
    @ColumnInfo(name = "status")
    val status: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)