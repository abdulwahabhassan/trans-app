package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.AgentRouteEntity
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.database.entity.VehicleCurrentEntity
import ng.gov.imostate.database.entity.VehicleRouteEntity
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class NfcReaderResultFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository,
    private val vehicleRepository: VehicleRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun getRateInDatabase(category: String): RateEntity? {
        return agentRepository.getRateInDatabase(category)
    }

    suspend fun getAllAgentsRouteInDatabase(): List<AgentRouteEntity> {
        return agentRepository.getAllAgentRoutesInDatabase()
    }

    suspend fun getVehicleFromCurrentEnumerationInDatabase(identifier: String): VehicleCurrentEntity? {
        return vehicleRepository.getVehicleFromCurrentEnumerationInDatabase(identifier)
    }
}