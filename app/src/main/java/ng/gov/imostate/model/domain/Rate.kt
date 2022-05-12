package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Rate (
    val id: Long,
    val from: String?,
    val to: String?,
    val status: String?,
    val amount: String?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val category: String?
)