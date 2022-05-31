package ng.gov.imostate.model.domain

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class Holiday (
    val id: Long?,
    val title: String?,
    val date: String?,
    @Json(name = "route_id")
    val routeId: Long?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val status: String?
        )