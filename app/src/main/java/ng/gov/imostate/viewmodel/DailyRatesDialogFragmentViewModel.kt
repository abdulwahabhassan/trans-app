package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.model.apiresult.RatesResult
import ng.gov.imostate.model.request.GetVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class DailyRatesDialogFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(
    userPreferencesRepository
){
    suspend fun getRates(): ViewModelResult<List<RateEntity>> {
        val rates = agentRepository.getRatesInDatabase()
        return if (rates.isNotEmpty()) {
            ViewModelResult.Success(rates)
        } else {
            ViewModelResult.Error("No rates available")
        }
    }
}