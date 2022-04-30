package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.apiresult.LoginResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor (
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
): BaseViewModel(userPreferencesRepository) {

    suspend fun loginAttendant(agentLoginRequest: LoginRequest): ViewModelResult<LoginResult?> {
        val response = agentRepository.loginAgent(agentLoginRequest)
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
