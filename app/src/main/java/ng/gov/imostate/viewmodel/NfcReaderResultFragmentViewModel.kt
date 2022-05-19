package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import ng.gov.imostate.repository.VehicleRepository
import javax.inject.Inject

@HiltViewModel
class NfcReaderResultFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun getRateInDatabase(category: String): RateEntity? {
        return agentRepository.getRateInDatabase(category)
    }

}