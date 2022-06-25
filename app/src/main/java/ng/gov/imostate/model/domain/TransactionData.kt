package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class TransactionData(
    @Json(name = "vehicle_id")
    val vehicleId: String?,
    @Json(name = "to")
    val to: String?,
    @Json(name = "total_amount")
    val totalAmount: String,
    @Json(name = "total_days")
    val totalDays: String,
    @Json(name = "collection_time")
    val collectionTime: String
)
