package ng.gov.imostate.model.domain

data class Transaction (
    val id: String,
    val title: String,
    val amount: Double,
    val date: String
        )