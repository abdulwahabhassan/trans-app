package ng.gov.imostate.model.apiresult

import ng.gov.imostate.model.domain.AccountDetails

data class FundWalletAccountDetailsResult(
    val accounts: List<AccountDetails>
)