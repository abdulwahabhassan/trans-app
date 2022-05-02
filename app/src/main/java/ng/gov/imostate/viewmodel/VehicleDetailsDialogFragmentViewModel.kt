package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.apiresult.OnboardVehicleResult
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject
@HiltViewModel
class VehicleDetailsDialogFragmentViewModel @Inject constructor(
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

}