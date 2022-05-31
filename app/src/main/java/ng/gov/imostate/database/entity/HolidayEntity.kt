package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ng.gov.imostate.model.domain.Route

@Entity(tableName = "holiday")
data class HolidayEntity (
    @PrimaryKey
    val id: Long?,
    @ColumnInfo
    val title: String?,
    @ColumnInfo
    val date: String?,
    @ColumnInfo(name = "route_id")
    val routeId: Long?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo
    val status: String?
    )