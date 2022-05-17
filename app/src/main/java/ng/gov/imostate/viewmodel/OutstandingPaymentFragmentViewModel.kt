package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.Mapper
import ng.gov.imostate.model.apiresult.SyncTransactionsResult
import ng.gov.imostate.model.apiresult.TransactionsResult
import ng.gov.imostate.model.domain.TransactionData
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class OutstandingPaymentFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun insertTransactionToDatabase(transaction: TransactionData) {
        transactionRepository.insertTransactionToDatabase(
            Mapper.mapTransactionToTransactionEntity(transaction)
        )
    }

}