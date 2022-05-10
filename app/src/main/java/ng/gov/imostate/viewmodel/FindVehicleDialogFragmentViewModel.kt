package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class FindVehicleDialogFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository
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

    suspend fun findVehicleInDatabase(identifier: String): VehicleEntity? {
        return vehicleRepository.findVehicleInDatabase(identifier)
    }

    suspend fun findVehicleDriverRecordInDatabase(identifier: String): VehicleEntity? {
        return vehicleRepository.findVehicleDriverRecordInDatabase(identifier)
    }

    suspend fun getAllVehiclesInDatabase(): List<VehicleEntity> {
        return vehicleRepository.getAllVehiclesInDatabase()
    }

    suspend fun getAllDriversInDatabase(): List<DriverEntity> {
        return vehicleRepository.getAllDriversInDatabase()
    }

}