package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.apiresult.VehicleResult
import ng.gov.imostate.model.domain.Vehicle
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
//    suspend fun getVehicle(token: String, vehicleId: String): ViewModelResult<VehicleResult?> {
//        val response = vehicleRepository.getVehicle(token, vehicleId)
//        return  when (response.success) {
//            true -> {
//                ViewModelResult.Success(response.result)
//            }
//            else -> {
//                ViewModelResult.Error(response.message ?: "Unknown Error")
//            }
//        }
//    }

    suspend fun getVehicle(token: String, vehicleId: String): VehicleResult {
        return vehicleRepository.getVehicle(token, vehicleId)
    }

//    suspend fun findVehicleInDatabase(identifier: String): VehicleEntity? {
//        return vehicleRepository.findVehicleInDatabase(identifier)
//    }

    suspend fun findVehicleDriverRecordInDatabase(identifier: String): VehicleEntity? {
        return vehicleRepository.findVehicleDriverRecordInDatabase(identifier)
    }

//    suspend fun getAllVehiclesInDatabase(): List<VehicleEntity> {
//        return vehicleRepository.getAllVehiclesInDatabase()
//    }
//
//    suspend fun getAllDriversInDatabase(): List<DriverEntity> {
//        return vehicleRepository.getAllDriversInDatabase()
//    }

}