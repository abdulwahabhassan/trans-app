package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.Vehicle

data class SyncTransactionsResult(
    val status: String?,
    val vehicles: List<Vehicle>?
)
