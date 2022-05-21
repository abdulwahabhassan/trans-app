package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Collection (
    val id: Long?,
    @Json(name = "vehicle_id")
    val vehicleID: Long?,
    @Json(name = "agent_user_id")
    val agentUserID: Long?,
    @Json(name = "days_count")
    val daysCount: String?,
    val status: String?,
    val date: String?,
    @Json(name = "transaction_id")
    val transactionID: Long?,
    val amount: String?,
    @Json(name = "disbursement_id")
    val disbursementID: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    @Json(name = "internal_reference")
    val internalReference: String?,
    @Json(name = "flagged_message")
    val flaggedMessage: String?,
    @Json(name = "points_aggregated")
    val pointsAggregated: Long?
)