package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "rate")
class RateEntity (
    @PrimaryKey
    @ColumnInfo
    val id: Long?,
    @ColumnInfo
    val from: String?,
    @ColumnInfo
    val to: String?,
    @ColumnInfo
    val status: String?,
    @ColumnInfo
    val amount: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo
    val category: String?
    )
