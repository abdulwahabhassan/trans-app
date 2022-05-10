package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Vehicle(
    val id: Long?,
    @Json(name = "vehicle_plates")
    val vehiclePlates: String?,
    val brand: String?,
    val type: String?,
    @Json(name = "fleet_number")
    val fleetNumber: String?,
    @Json(name = "state_of_registration")
    val stateOfRegistration: String?,
    @Json(name = "chasis_number")
    val chasisNumber: String?,
    @Json(name = "engine_number")
    val engineNumber: String?,
    @Json(name = "vehicle_model")
    val vehicleModel: String?,
    @Json(name = "passenger_capacity")
    val passengerCapacity: String?,
    @Json(name = "road_worthiness_exp_date")
    val roadWorthinessExpDate: String?,
    @Json(name = "vehicle_licence_exp_date")
    val vehicleLicenceExpDate: String?,
    @Json(name = "vehicle_insurance_exp_date")
    val vehicleInsuranceExpDate: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val driver: Driver?,
    val routes: List<Route>?
)

