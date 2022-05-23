package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class MMetrics (
    val metrics: Metrics,
    val points: List<Point>
)

data class Metrics (
    @Json(name = "current_balance")
    val currentBalance: String?,
    @Json(name = "total_amount_credited")
    val totalAmountCredited: String?,
    @Json(name = "total_amount_vended")
    val totalAmountVended: String?,
    @Json(name = "current_payable")
    val currentPayable: String?,
    @Json(name = "paid_out")
    val paidOut: String?,
    @Json(name = "created_by")
    val createdBy: Long?,
    @Json(name = "last_paid")
    val lastPaid: String?,
    val photo: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?
)

