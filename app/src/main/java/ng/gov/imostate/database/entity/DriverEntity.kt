package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

@Entity(tableName = "driver")
data class DriverEntity(
    @PrimaryKey
    @ColumnInfo
    val id: Long?,
    @ColumnInfo(name = "first_name")
    val firstName: String?,
    @ColumnInfo(name = "middle_name")
    val middleName: String?,
    @ColumnInfo(name = "last_name")
    val lastName: String?,
    @ColumnInfo(name = "d_o_b")
    val dOB: String?,
    @ColumnInfo
    val address: String?,
    @ColumnInfo
    val email: String?,
    @ColumnInfo
    val gender: String?,
    @ColumnInfo(name = "marital_status")
    val maritalStatus: String?,
    @ColumnInfo(name = "blood_group")
    val bloodGroup: String?,
    @ColumnInfo(name = "means_of_id")
    val meansOfID: String?,
    @ColumnInfo(name = "id_number")
    val idNumber: String?,
    @ColumnInfo(name = "state_of_origin")
    val stateOfOrigin: String?,
    @ColumnInfo(name = "local_govt")
    val localGovt: String?,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "account_number")
    val accountNumber: String?,
    @ColumnInfo
    val bvn: String?,
    @ColumnInfo
    val phone: String?,
    @ColumnInfo
    val imssin: String?,
    @ColumnInfo(name = "json_data")
    val jsonData: String?,
    @ColumnInfo(name = "vehicle_id")
    val vehicleID: Long?,
    val status: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String?,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?
)
