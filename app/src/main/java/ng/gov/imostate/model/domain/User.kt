package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class User (
    val id: Long?,
    val name: String?,
    val type: String?,
    val phone: String?,
    val address: String?,
    @Json(name = "business_name")
    val businessName: String?,
    @Json(name = "onboarding_date")
    val onboardingDate: String?,
    val email: String?,
    @Json(name = "email_verified_at")
    val emailVerifiedAt: String?,
    val status: String?,
    @Json(name = "created_by")
    val createdBy: Long?,
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val bvn: String?,
    val profile: Profile?,
)
