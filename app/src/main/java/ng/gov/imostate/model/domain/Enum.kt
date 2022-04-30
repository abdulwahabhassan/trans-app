package ng.gov.imostate.model.domain

enum class Api(val baseUrl: String) {
    PROD(baseUrl = "http://https://www.imostate.gov.ng/api/v1/"),
    STAGING(baseUrl = "https://stormy-plateau-90516.herokuapp.com/api/v1/")
}