package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.Transaction

data class TransactionsResult(
    val transactions: List<Transaction>
)
