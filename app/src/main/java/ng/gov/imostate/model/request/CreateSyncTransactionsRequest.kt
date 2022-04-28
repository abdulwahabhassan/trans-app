package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class CreateSyncTransactionsRequest (
    @Json(name = "data")
    val data: List<TransactionData>
)