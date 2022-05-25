package ng.gov.imostate.model.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class AgentRoute(
    val id: Long? = null,
    @Json(name = "user_id")
    val userId: Long? = null,
    @Json(name = "route_id")
    val routeId: Long? = null,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null
)
