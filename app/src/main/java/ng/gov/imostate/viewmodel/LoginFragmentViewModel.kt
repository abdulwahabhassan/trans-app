package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.model.request.LoginRequest
import ng.gov.imostate.model.response.LoginResult
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.AppConfigRepository
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor (
    appConfigRepository: AppConfigRepository,
    private val agentRepository: AgentRepository
): BaseViewModel(appConfigRepository) {

    suspend fun loginAttendant(agentLoginRequest: LoginRequest): ViewModelResult<LoginResult?> {
        val response = agentRepository.loginAgent(agentLoginRequest)
        appConfigRepository.updateAgentLog(true)
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
