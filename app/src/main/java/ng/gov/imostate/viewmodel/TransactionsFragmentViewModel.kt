package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.apiresult.SyncTransactionsResult
import ng.gov.imostate.model.apiresult.TransactionsResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject


@HiltViewModel
class TransactionsFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(
    userPreferencesRepository
){

    suspend fun getTransactions(token: String,): ViewModelResult<TransactionsResult?> {
        val response = transactionRepository.getTransactions(token)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun createSyncTransactions(token: String, vehicleId: Int, data: CreateSyncTransactionsRequest): ViewModelResult<SyncTransactionsResult?> {
        val response = transactionRepository.createSyncTransactions(token, vehicleId, data)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

}