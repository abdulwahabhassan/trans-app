package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.response.OnboardVehicleResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.VehicleRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject


@HiltViewModel
class AddVehicleFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository
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

}