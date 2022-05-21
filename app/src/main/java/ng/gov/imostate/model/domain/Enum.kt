package ng.gov.imostate.model.domain

enum class Api(val baseUrl: String) {
    PROD(baseUrl = "https://transapp-api.occe.ng/api/v1/"),
    STAGING(baseUrl = "https://stormy-plateau-90516.herokuapp.com/api/v1/")
}

enum class TransactionType {
    AGENT_TRANSACTION,
    VEHICLE_TRANSACTION
}