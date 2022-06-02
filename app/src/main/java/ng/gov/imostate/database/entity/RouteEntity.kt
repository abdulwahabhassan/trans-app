package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey
    @ColumnInfo
    val id: Long? = null,
    val from: String? = null,
    val to: String? = null,
    val status: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,
    var selected: Boolean? = false
)
