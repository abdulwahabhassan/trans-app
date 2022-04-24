package ng.gov.imostate.model

enum class Api(val baseUrl: String) {
    PROD(baseUrl = "http://https://www.imostate.gov.ng/api/v1/"),
    STAGING(baseUrl = "http://https://www.imostate.gov.ng/api/v1/")
}