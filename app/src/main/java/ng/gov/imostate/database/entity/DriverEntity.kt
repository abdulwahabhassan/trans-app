package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "driver")
data class DriverEntity(
    @PrimaryKey val id: Long?,
    @SerializedName("first_name")
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    @SerializedName("middle_name")
    @ColumnInfo(name = "middle_name")
    val middleName: String?,
    @SerializedName("last_name")
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    @SerializedName("d_o_b")
    @ColumnInfo(name = "d_o_b")
    val dOB: String?,
    @ColumnInfo
    val address: String?,
    @ColumnInfo
    val email: String?,
    @ColumnInfo
    val gender: String?,
    @SerializedName("marital_status")
    @ColumnInfo(name = "marital_status")
    val maritalStatus: String?,
    @SerializedName("blood_group")
    @ColumnInfo(name = "blood_group")
    val bloodGroup: String?,
    @SerializedName("means_of_id")
    @ColumnInfo(name = "means_of_id")
    val meansOfID: String?,
    @SerializedName("id_number")
    @ColumnInfo(name = "id_number")
    val idNumber: String?,
    @SerializedName("state_of_origin")
    @ColumnInfo(name = "state_of_origin")
    val stateOfOrigin: String?,
    @SerializedName("local_govt")
    @ColumnInfo(name = "local_govt")
    val localGovt: String?,
    @SerializedName("bank_name")
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @SerializedName("account_number")
    @ColumnInfo(name = "account_number")
    val accountNumber: String?,
    @ColumnInfo
    val bvn: String?,
    @ColumnInfo
    val phone: String?,
    @ColumnInfo
    val imssin: String?,
    @SerializedName("json_data")
    @ColumnInfo(name = "json_data")
    val jsonData: String?,
    @SerializedName("vehicle_id")
    @ColumnInfo(name = "vehicle_id")
    val vehicleID: Long?,
    val status: String?,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?
)
