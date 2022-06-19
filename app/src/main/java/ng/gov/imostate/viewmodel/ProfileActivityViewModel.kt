package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.AgentRouteEntity
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.model.apiresult.MetricsResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject


@HiltViewModel
class ProfileActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(
    userPreferencesRepository
) {
    suspend fun getDashBoardMetrics(token: String,): ViewModelResult<MetricsResult?> {
        val response = agentRepository.getDashBoardMetrics(token)
        return  when (response.success) {
            true -> {
                ViewModelResult.Success(response.result)
            }
            else -> {
                ViewModelResult.Error(response.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getAllAgentRoutesInDatabase(): List<AgentRouteEntity> {
        return agentRepository.getAllAgentRoutesInDatabase()
    }

    suspend fun getAllRoutesInDatabase(): List<RouteEntity> {
        return agentRepository.getAllRoutesInDatabase()
    }
}