package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "transaction")
data class TransactionEntity (
    @PrimaryKey
    @ColumnInfo(name = "vehicle_id")
    val vehicleId: String,
    @ColumnInfo
    val to: String?,
    @ColumnInfo(name = "total_amount")
    val totalAmount: String,
    @ColumnInfo(name = "total_days")
    val totalDays: String,
    @ColumnInfo(name = "collection_time")
    val collectionTime: String
    )