package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.Mapper
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.apiresult.OnboardVehicleResult
import ng.gov.imostate.model.domain.TransactionData
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject


@HiltViewModel
class AddVehicleFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
) : BaseViewModel(
    userPreferencesRepository
){

    suspend fun onboardVehicle(token: String, onboardVehicleRequest: OnboardVehicleRequest): ViewModelResult<OnboardVehicleResult?> {
        val response = vehicleRepository.onboardVehicle(token, onboardVehicleRequest)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun findVehicleDriverRecordInDatabase(identifier: String): VehicleEntity? {
        return vehicleRepository.findVehicleDriverRecordInDatabase(identifier)
    }

    suspend fun insertTransactionToDatabase(transaction: TransactionData) {
        transactionRepository.insertTransactionToDatabase(
            Mapper.mapTransactionToTransactionEntity(transaction)
        )
    }

}