package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class Route (
    val id: Long? = null,
    val from: String? = null,
    val to: String? = null,
    val status: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null,
    @Json(name = "updated_at")
    val updatedAt: String? = null,
        )
