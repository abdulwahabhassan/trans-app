package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transaction")
data class TransactionEntity (
    @PrimaryKey
    @ColumnInfo(name = "vehicle_id")
    val vehicleId: String,
    @ColumnInfo
    val to: String?,
    @ColumnInfo
    val amount: Double?,
    @ColumnInfo(name = "date_time")
    val dateTime: String?
    )