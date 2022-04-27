package ng.gov.imostate.model.request

import com.squareup.moshi.Json

data class CreateTransactionRequest (
    @Json(name = "data")
    val data: List<TransactionData>
)