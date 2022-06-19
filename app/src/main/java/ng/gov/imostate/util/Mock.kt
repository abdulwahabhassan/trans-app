package ng.gov.imostate.util

import ng.gov.imostate.model.domain.AccountDetails
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.model.domain.Update

object Mock {
    fun getFundWalletAccountDetails(): List<AccountDetails> {
        return listOf(
            AccountDetails("John Maxwell", "0123902102", "First Bank of Nigeria Limited"),
            AccountDetails("Dumebi Akpan Michael", "2112163770", "Stanbic IBTC Bank Plc"),
            AccountDetails("Esther Francis", "9002171720", "Ecobank Nigeria"),
            AccountDetails("Mohammed Toafiq", "0123902102", "United Bank for Africa Plc"),
            AccountDetails("Rochas Madu Anselm", "2112163770", "Zenith Bank Plc"),
            AccountDetails("BGT Firm Limited", "9002171720", "Polaris Bank Limited")
        )
    }
}