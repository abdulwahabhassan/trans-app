package ng.gov.imostate.model.domain

import com.squareup.moshi.Json

data class AccountDetails (
    @Json(name = "account_name")
    val accountName: String?,
    @Json(name = "account_number")
    val accountNumber: String?,
    @Json(name = "bank_name")
    val bankName: String?,
        )