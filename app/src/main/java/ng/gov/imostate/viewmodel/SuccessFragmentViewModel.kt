package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.apiresult.SyncTransactionsResult
import ng.gov.imostate.model.apiresult.TransactionResult
import ng.gov.imostate.model.apiresult.TransactionsResult
import ng.gov.imostate.model.request.CreateSyncTransactionsRequest
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class SuccessFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun getVehicle(token: String, getVehicleRequest: GetVehicleRequest): ViewModelResult<Any?> {
        val response = vehicleRepository.getVehicle(token, getVehicleRequest)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getRecentTransaction(token: String, vehicleId: String): ViewModelResult<TransactionResult?> {
        val response = transactionRepository.getRecentTransaction(token, vehicleId)
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