package ng.gov.imostate.model.request

import com.squareup.moshi.Json
import ng.gov.imostate.model.domain.TransactionData

data class CreateSyncTransactionsRequest (
    @Json(name = "data")
    val data: List<TransactionData>
)