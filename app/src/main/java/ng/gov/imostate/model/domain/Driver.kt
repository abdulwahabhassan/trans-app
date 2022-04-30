package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Driver(
    val id: Long?,
    @Json(name = "first_name")
    val firstName: String?,
    @Json(name = "middle_name")
    val middleName: String?,
    @Json(name = "last_name")
    val lastName: String?,
    @Json(name = "d_o_b")
    val dOB: String?,
    val address: String?,
    val email: String?,
    val gender: String?,
    @Json(name = "marital_status")
    val maritalStatus: String?,
    @Json(name = "blood_group")
    val bloodGroup: String?,
    @Json(name = "means_of_id")
    val meansOfID: String?,
    @Json(name = "id_number")
    val idNumber: String?,
    @Json(name = "state_of_origin")
    val stateOfOrigin: String?,
    @Json(name = "local_govt")
    val localGovt: String?,
    @Json(name = "bank_name")
    val bankName: String?,
    @Json(name = "account_number")
    val accountNumber: String?,
    val bvn: String?,
    val phone: String?,
    val imssin: String?,
    @Json(name = "json_data")
    val jsonData: String?,
    @Json(name = "vehicle_id")
    val vehicleID: Long?,
    val status: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)
