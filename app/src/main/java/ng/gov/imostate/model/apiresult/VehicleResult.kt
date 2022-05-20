package ng.gov.imostate.model.apiresult

import com.squareup.moshi.Json

data class VehicleResult (
    val id: Long,
    @Json(name = "vehicle_plates")
    val vehiclePlates: String,
    val brand: String,
    val type: String,
    @Json(name = "fleet_number")
    val fleetNumber: String,
    @Json(name = "state_of_registration")
    val stateOfRegistration: String,
    @Json(name = "chasis_number")
    val chasisNumber: String,
    @Json(name = "engine_number")
    val engineNumber: String,
    @Json(name = "vehicle_model")
    val vehicleModel: String,
    @Json(name = "passenger_capacity")
    val passengerCapacity: String,
    @Json(name = "road_worthiness_exp_date")
    val roadWorthinessExpDate: String,
    @Json(name = "vehicle_licence_exp_date")
    val vehicleLicenceExpDate: String,
    @Json(name = "vehicle_insurance_exp_date")
    val vehicleInsuranceExpDate: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "last_paid")
    val lastPaid: String,
    val collection: List<Collection>
)

data class Collection (
    val id: Long,
    @Json(name = "vehicle_id")
    val vehicleID: Long,
    @Json(name = "agent_user_id")
    val agentUserID: Long,
    @Json(name = "days_count")
    val daysCount: String,
    val status: String,
    val date: String,
    @Json(name = "transaction_id")
    val transactionID: Long,
    val amount: String,
    @Json(name = "disbursement_id")
    val disbursementID: String?,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "internal_reference")
    val internalReference: String,
    @Json(name = "flagged_message")
    val flaggedMessage: String,
    @Json(name = "points_aggregated")
    val pointsAggregated: Long?
)
