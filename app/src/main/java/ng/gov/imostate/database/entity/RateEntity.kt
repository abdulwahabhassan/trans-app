package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "rate")
class RateEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long?,
    @ColumnInfo(name = "from")
    val from: String?,
    @ColumnInfo(name = "to")
    val to: String?,
    @ColumnInfo(name = "status")
    val status: String?,
    @ColumnInfo(name = "amount")
    val amount: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo(name = "category")
    val category: String?
    )
