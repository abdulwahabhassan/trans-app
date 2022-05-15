package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class FundWalletAccountDetailsRequest (
    @Json(name = "agentId")
    val agentId: String
        )