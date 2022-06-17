package ng.gov.imostate.model.apiresult

import com.squareup.moshi.Json
import ng.gov.imostate.model.domain.AccountDetails
import ng.gov.imostate.model.domain.User

data class CurrentUserResult(
    val user: User?,
)
