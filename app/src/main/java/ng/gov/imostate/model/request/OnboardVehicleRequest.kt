package ng.gov.imostate.model.request

import com.squareup.moshi.Json
import ng.gov.imostate.model.domain.VehicleRoute

data class OnboardVehicleRequest (
    @Json(name = "vehicle_plates")
    val vehiclePlates: String,
    @Json(name = "fleet_number")
    val fleetNumber: String,
    val brand: String,
    val type: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "middle_name")
    val middleName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "d_o_b")
    val dOB: String,
    val address: String,
    val imssin: String,
    val email: String,
    val phone: String,
    val vehicleRoutes: List<VehicleRoute>,
    @Json(name = "state_of_registration")
    val stateOfRegistration: String,
    val gender: String,
    @Json(name = "marital_status")
    val maritalStatus: String,
    @Json(name = "blood_group")
    val bloodGroup: String,
    @Json(name = "passenger_capacity")
    val passengerCapacity: String,
    @Json(name = "chasis_number")
    val chasisNumber: String,
    @Json(name = "engine_number")
    val engineNumber: String,
    @Json(name = "vehicle_model")
    val vehicleModel: String,
    @Json(name = "means_of_id")
    val meansOfID: String,
    @Json(name = "id_number")
    val idNumber: String,
    @Json(name = "state_of_origin")
    val stateOfOrigin: String,
    @Json(name = "local_govt")
    val localGovt: String,
    @Json(name = "bank_name")
    val bankName: String,
    @Json(name = "account_number")
    val accountNumber: String,
    val bvn: String,
    @Json(name = "road_worthiness_exp_date")
    val roadWorthinessExpDate: String,
    @Json(name = "vehicle_licence_exp_date")
    val vehicleLicenceExpDate: String,
    @Json(name = "vehicle_insurance_exp_date")
    val vehicleInsuranceExpDate: String
)
