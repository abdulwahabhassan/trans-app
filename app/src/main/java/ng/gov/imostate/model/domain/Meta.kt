package ng.gov.imostate.model.domain

import com.squareup.moshi.Json


data class Meta(
    val route: List<Route>?,
    val rates: List<Rate>?,
    @Json(name = "monnify_accounts")
    val monnifyAccounts: List<AccountDetails>?,
    @Json(name = "server_time")
    val serverTime: String?
)

