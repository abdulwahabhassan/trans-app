package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.database.entity.VehiclePreviousEntity
import ng.gov.imostate.model.apiresult.OnboardVehicleResult
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject


@HiltViewModel
class AddVehicleFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository,
    private val agentRepository: AgentRepository
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

    suspend fun getAllRoutesInDatabase(): List<RouteEntity> {
        return agentRepository.getAllRoutesInDatabase()
    }

    suspend fun findVehicleDriverRecordFromPreviousEnumerationInDatabase(identifier: String): VehiclePreviousEntity? {
        return vehicleRepository.findVehicleDriverRecordFromPreviousEnumerationInDatabase(identifier)
    }

    suspend fun getCategories(): List<String> {
        return agentRepository.getAllCategoriesInDatabase()
    }

    suspend fun getRoute(routeId: Long): RouteEntity? {
        return vehicleRepository.getRoute(routeId)
    }

}