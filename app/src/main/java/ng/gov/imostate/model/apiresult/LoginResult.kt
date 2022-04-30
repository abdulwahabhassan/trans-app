package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.User

data class LoginResult (
    val token: String? = null,
    val user: User? = null
)
