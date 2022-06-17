package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.*
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.TransactionRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class NfcReaderResultFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository,
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
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

    suspend fun getAllHolidaysInDatabase(): List<HolidayEntity> {
        return transactionRepository.getAllTaxFreeDaysInDatabase()
    }
}